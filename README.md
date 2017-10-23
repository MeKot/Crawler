# Crawler
A small side project on writing a concurrent web crawler using Java


Crawler parses the HTML of the page with regex to find the urls to visit and runs on them.

Provides useful error messages in the log:

-IOException for pages that were identified as URLs, but the Crawler was unable to buffer

-MalformedURLException for the pages it was unable to identify as URLs
