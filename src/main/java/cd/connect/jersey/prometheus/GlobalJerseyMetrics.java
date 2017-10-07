package cd.connect.jersey.prometheus;

import io.prometheus.client.Counter;

import java.util.function.Function;

/**
 * @author Richard Vowles - https://plus.google.com/+RichardVowles
 */
public class GlobalJerseyMetrics {
  public static Counter requests;
  public static Counter response_5xx;
  public static Counter response_4xx;
  public static Counter response_3xx;
  public static Counter response_2xx;
  
  static void init(final String prefix) {
    if (requests == null) {
      Function<String, String> name = s -> {
        return (prefix == null || prefix.length() == 0) ? s : (prefix + "_" + s);
      };

      requests = Counter.build()
        .name(name.apply("requests_total")).help("Total tracked requests.").register();
      response_5xx = Counter.build().name(name.apply("response_5xx")).help("5xx response count").register();
      response_4xx = Counter.build().name(name.apply("response_4xx")).help("4xx response count").register();
      response_3xx = Counter.build().name(name.apply("response_3xx")).help("3xx response count").register();
      response_2xx = Counter.build().name(name.apply("response_2xx")).help("2xx response count").register();
    }
  }
}
