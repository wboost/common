package top.wboost.common.utils.web.interfaces.context;

import java.util.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;

import org.springframework.core.OrderComparator;
import top.wboost.common.util.AnnotationUtil;
import top.wboost.common.utils.web.utils.SpringApplicationUtil;

public interface EzApplicationListenerManager extends ApplicationListener<ApplicationEvent> {

	default void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextClosedEvent) {
			onContextClosedEvent((ContextClosedEvent) event);
		} else if (event instanceof ContextRefreshedEvent) {
			onContextRefreshedEvent((ContextRefreshedEvent) event);
		} else if (event instanceof ContextStartedEvent) {
			onContextStartedEvent((ContextStartedEvent) event);
		} else if (event instanceof ContextStoppedEvent) {
			onContextStoppedEvent((ContextStoppedEvent) event);
		} else {
			// TODO
		}
	}

	default void onContextStoppedEvent(ContextStoppedEvent event) {

	}

	default void onContextStartedEvent(ContextStartedEvent event) {

	}

	default void onContextClosedEvent(ContextClosedEvent event) {

	}

	default void onContextRefreshedEvent(ContextRefreshedEvent event) {
		ApplicationContext parentContext = event.getApplicationContext().getParent();
		List<EzApplicationListener> ezApplicationListeners = new ArrayList<>(getEzApplicationListeners(event.getApplicationContext()));
        ezApplicationListeners.sort(OrderComparator.INSTANCE);
		// 设置root优先级大于web
//		Set<EzApplicationListener> webApplicationListeners = new LinkedHashSet<>();
//		Set<EzApplicationListener> rootApplicationListeners = new LinkedHashSet<>();
		if (!SpringApplicationUtil.isBootApplicationContext(event)) {
			for (EzApplicationListener ezApplicationListener : ezApplicationListeners) {
				// no spring boot
				if (parentContext != null) {
					ezApplicationListener.onWebApplicationEvent(event);
				} else {
					ezApplicationListener.onRootApplicationEvent(event);
				}
			}
		} else {
			for (EzApplicationListener ezApplicationListener : ezApplicationListeners) {
				// spring boot
				ezApplicationListener.onBootApplicationEvent(event);
				if (ezApplicationListener.doWebAndRootApplicationListener(event)) {
					ezApplicationListener.onRootApplicationEvent(event);
				}
			}
			for (EzApplicationListener ezApplicationListener : ezApplicationListeners) {
				if (ezApplicationListener.doWebAndRootApplicationListener(event)) {
					ezApplicationListener.onWebApplicationEvent(event);
				}
			}
		}
	}

	Collection<EzApplicationListener> getEzApplicationListeners(ApplicationContext applicationContext);

	void setEzApplicationListeners(Collection<EzApplicationListener> ezApplicationListeners);

}
