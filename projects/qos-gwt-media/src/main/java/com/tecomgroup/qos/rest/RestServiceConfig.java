
package com.tecomgroup.qos.rest;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author stroganov.d
 */
public class RestServiceConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(MultiPartFeature.class);
        resources.add(ChannelConfigurationRESTFacade.class);
        resources.add(ChannelStateRESTFacade.class);
        resources.add(ChannelActionRESTFacade.class);
        resources.add(ProbeConfigurationRESTFacade.class);
        resources.add(RecordingSchedulerRESTFacade.class);
        resources.add(Widgets.class);
		resources.add(RbacRolesRESTFacade.class);
    }

    @Override
    public Set<Object> getSingletons() {
        final Set<Object> instances = new HashSet<Object>();

        instances.add(new JacksonJsonProvider());
        return instances;
    }
}
