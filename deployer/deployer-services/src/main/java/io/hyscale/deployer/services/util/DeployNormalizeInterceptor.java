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
package io.hyscale.deployer.services.util;

import io.hyscale.commons.annotations.ComponentInterceptor;
import io.hyscale.commons.annotations.Normalize;
import io.hyscale.commons.exception.CommonErrorCode;
import io.hyscale.commons.exception.HyscaleException;
import io.hyscale.commons.models.DeploymentContext;
import io.hyscale.deployer.services.processor.DeployerInterceptorProcessor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Component
public class DeployNormalizeInterceptor extends DeployerInterceptorProcessor {

    @Override
    protected void _preProcess(DeploymentContext context) throws HyscaleException {
        Class ComponentClass = context.getClass();
        Field[] fields = ComponentClass.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
            }
            Normalize annotation = field.getDeclaredAnnotation(Normalize.class);
            if (annotation != null && field.getType().getClass().isInstance(String.class)) {
                try {
                    field.set(context, annotation.entity().normalize(field.get(context).toString()));
                } catch (IllegalAccessException e) {
                    throw new HyscaleException(e, CommonErrorCode.ILLEGAL_ACCESS, field.getName());
                }
            }
        }
    }

    @Override
    protected void _postProcess(DeploymentContext context) throws HyscaleException {

    }

    @Override
    protected void _onError(DeploymentContext context) throws HyscaleException {

    }

    public static String getId() {
        return DeployNormalizeInterceptor.class.getCanonicalName();
    }
}