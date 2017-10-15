import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * An actual crawling class, that will work the heavy lifting.
 */
public class CrawlingTread implements Runnable {

  @Override
  public void run() {
    while (!WebCrawler.targets.isEmpty()) {

      if (WebCrawler.visited.size() > WebCrawler.NUMBER_OF_PAGES_CRAWLED) return;

      URL url;
      BufferedReader br = null;
      boolean fileReadOk = false;
      String currentUrl = WebCrawler.targets.poll();
      while (!fileReadOk) {
        try {
          url = new URL(currentUrl);
          br = new BufferedReader(new InputStreamReader(url.openStream()));
          fileReadOk = true;
        } catch (MalformedURLException e) {
          System.out.println("=== MalformedURLException : " + currentUrl + " ===");
          currentUrl = WebCrawler.targets.poll();
          fileReadOk = false;
        } catch (IOException ioe) {
          System.out.println("=== IOException : " + currentUrl + " ===");
          currentUrl = WebCrawler.targets.poll();
          fileReadOk = false;
        }
      }
      //Could this expose a DDoS attack if the page has a lot of lines that will result in heap overflow..?
      //I'll assume no for now and come back later, if have time
      String page = br.lines().collect(Collectors.joining());
      Pattern urlPattern = Pattern.compile(WebCrawler.REGEX);
      Matcher matcher = urlPattern.matcher(page);

      while (matcher.find()) {
        String newUrl = matcher.group();

        if (!WebCrawler.visited.contains(newUrl)) {
          WebCrawler.visited.add(newUrl);
          WebCrawler.targets.add(newUrl);
          System.out.println("Site added to be crawled : " + newUrl);
        }
      }
    }
  }
}
