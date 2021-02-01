/**
 * Copyright 2019 Pramati Prism, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.hyscale.generator.services.builder;

import io.hyscale.commons.exception.HyscaleException;
import io.hyscale.commons.models.ConfigTemplate;
import io.hyscale.commons.models.LoadBalancer;
import io.hyscale.commons.models.ServiceMetadata;
import io.hyscale.commons.utils.MustacheTemplateResolver;
import io.hyscale.generator.services.constants.ManifestGenConstants;
import io.hyscale.generator.services.model.ManifestResource;
import io.hyscale.generator.services.provider.PluginTemplateProvider;
import io.hyscale.plugin.framework.models.ManifestSnippet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GatewayBuilder implements IstioResourcesManifestGenerator {

    private static final String GATEWAY_LABELS = "labels";
    private static final String TLS_MODE = "TLS_MODE";
    private static final String DEFAULT_TLS_MODE = "SIMPLE";
    private static final String PROTOCOL = "PROTOCOL";
    private static final String PORT_NUMBER = "PORT_NUMBER";
    private static final Integer DEFAULT_GATEWAY_PORT = 80;

    @Autowired
    private PluginTemplateProvider templateProvider;

    @Autowired
    private MustacheTemplateResolver templateResolver;

    @Override
    public ManifestSnippet generateManifest(ServiceMetadata serviceMetadata, LoadBalancer loadBalancer) throws HyscaleException {
        ConfigTemplate gatewayTemplate = templateProvider.get(PluginTemplateProvider.PluginTemplateType.ISTIO_GATEWAY);
        String yaml = templateResolver.resolveTemplate(gatewayTemplate.getTemplatePath(), getContext(loadBalancer));
        ManifestSnippet snippet = new ManifestSnippet();
        snippet.setKind(ManifestResource.GATEWAY.getKind());
        snippet.setPath("spec");
        snippet.setSnippet(yaml);
        return snippet;
    }

    private Map<String, Object> getContext(LoadBalancer loadBalancer) {
        Map<String, Object> map = new HashMap<>();
        map.put(GATEWAY_LABELS, loadBalancer.getLabels().entrySet());
        map.put(ManifestGenConstants.LOADBALANCER, loadBalancer);
        List<String> hosts = new ArrayList<>();
        hosts.add(loadBalancer.getHost());
        map.put(ManifestGenConstants.HOSTS, hosts);
        map.put(TLS_MODE, DEFAULT_TLS_MODE);
        map.put(PORT_NUMBER, DEFAULT_GATEWAY_PORT);
        map.put(PROTOCOL, loadBalancer.getTlsSecret() != null ? "HTTPS" : "HTTP");
        return map;
    }
}
