package com.bluetrainsoftware.prometheus;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Richard Vowles - https://plus.google.com/+RichardVowles
 */
public class PrometheusFilter implements ApplicationEventListener {
	static final Counter requests = Counter.build()
		.name("requests_total").help("Total tracked requests.").register();

	protected String prefix = "";

	public PrometheusFilter() {
	}

	public PrometheusFilter(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public void onEvent(ApplicationEvent event) {
	}

	@Override
	public RequestEventListener onRequest(RequestEvent requestEvent) {
		return new PrometheusHistogramListener(prefix);
	}

	private static class PrometheusHistogramListener implements RequestEventListener {
		private static ConcurrentHashMap<Method, Histogram> histograms = new ConcurrentHashMap<>();
		private static ConcurrentHashMap<Method, Method> ignored = new ConcurrentHashMap<>();
		private Histogram.Timer timer;
		private final String prefix;

		private PrometheusHistogramListener(String prefix) {
			this.prefix = prefix;
		}

		private void resourceStart(RequestEvent matched) {
			Method method = matched.getUriInfo().getMatchedResourceMethod().getInvocable().getHandlingMethod();

			if (ignored.get(method) != null) {
				return; // seen it before, its ignored, lets get outta here
			}

			Histogram tracker = histograms.get(method);

			if (tracker == null) { // we don't know about it either way
				Prometheus instrument = method.getAnnotation(Prometheus.class);

				if (instrument != null) {
					tracker = Histogram.build().name(prefix + instrument.name()).help(instrument.help()).register();
					histograms.put(method, tracker);
				} else {
					ignored.put(method, method);
				}
			}

			if (tracker != null) {
				timer = tracker.startTimer();
				requests.inc();
			}
		}


		@Override
		public void onEvent(RequestEvent matched) {
			if (matched.getType() == RequestEvent.Type.RESOURCE_METHOD_START ) {
				resourceStart(matched);
			} else if (matched.getType() == RequestEvent.Type.RESOURCE_METHOD_FINISHED && timer != null) {
				timer.observeDuration();
				timer = null;
			}
		}
	}
}
