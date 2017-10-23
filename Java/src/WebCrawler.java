import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * The Main body of the crawler that spawns the threads and
 * controls their execution
 *
 * Operates 4 threads (not to overload the host machine)
 */
public class WebCrawler {

  //Constants

  private static final int N_OF_THREADS = 4;
  private static final int TIMEOUT = 10;
  static final int NUMBER_OF_PAGES_CRAWLED = 1000;
  static String REGEX = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}"
          + "\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";

  //Implemented as volatile as more fine-grained locks will not contribute to
  // the speed as such, due to the time it takes to download the page.
  static volatile Queue<String> targets = new LinkedList<>();
  static volatile Set<String> visited   = new HashSet<>();

  private ExecutorService pool;

  public WebCrawler() {
    this.pool  = newFixedThreadPool(N_OF_THREADS);
  }

  public void startCrawling(String initialUrl) throws InterruptedException{
    targets.add(initialUrl);
    pool.execute(new CrawlingTread());
    TimeUnit.SECONDS.sleep(2); //for the first thread to process a page
    for (int i = 1; i< N_OF_THREADS; ++i) {
      pool.execute(new CrawlingTread());
    }
    pool.awaitTermination(TIMEOUT, TimeUnit.SECONDS);
    if (pool.isTerminated()) {
      printResults();
      System.out.println("Pool terminated successfully");
    } else {
      pool.shutdownNow();
      printResults();
      System.out.println("Pool was shutdown");
    }
  }

  private void printResults() {
    System.out.println("++++++++++++ Pages crawled : ++++++++++++++");
    for (String s : visited) {
      System.out.println(s);
    }
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
    System.out.println(visited.size() + "---------------------------");
  }


  public static void main(String[] args) {
    if (args.length < 1) throw new RuntimeException("No starting page provided!");
    try {
      new WebCrawler().startCrawling(args[0]);
    } catch (InterruptedException e) {
      System.out.println("Interrupted Exception was thrown in the thread pool");
    }

  }

}