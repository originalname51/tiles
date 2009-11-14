/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tiles.template;

import org.apache.tiles.ArrayStack;
import org.apache.tiles.Attribute;
import org.apache.tiles.Definition;
import org.apache.tiles.mgmt.MutableTilesContainer;
import org.apache.tiles.request.Request;

/**
 * <p>
 * <strong>Create a definition at runtime. </strong>
 * </p>
 * <p>
 * Create a new definition at runtime. Newly created definition will be
 * available across the entire request.
 * </p>
 *
 * @version $Rev$ $Date$
 * @since 2.2.0
 */
public class DefinitionModel {

    /**
     * Starts the operation.
     *
     * @param composeStack The compose stack,
     * @param name The name of the definition to create. If not specified, an anonymous definition will be created.
     * @param template The template of this definition.
     * @param role A comma-separated list of roles. If present, the definition
     * will be rendered only if the current user belongs to one of the roles.
     * @param extendsParam The definition name that this definition extends.
     * @param preparer The preparer to use to invoke before the definition is rendered.
     * @since 2.2.0
     */
    public void start(ArrayStack<Object> composeStack, String name, String template,
            String role, String extendsParam, String preparer) {
        Definition definition = createDefinition(name, template, role,
                extendsParam, preparer);
        composeStack.push(definition);
    }

    /**
     * Ends the operation.
     *
     * @param container The Tiles container to use. It must be "mutable".
     * @param composeStack The compose stack.
     * @param request TODO
     * @since 2.2.0
     */
    public void end(MutableTilesContainer container,
            ArrayStack<Object> composeStack, Request request) {
        Definition definition = (Definition) composeStack.pop();
        registerDefinition(definition, container, composeStack, request);
    }

    /**
     * Executes the operation.
     *
     * @param container The Tiles container to use. It must be "mutable".
     * @param composeStack The compose stack.
     * @param name The name of the definition to create. If not specified, an anonymous definition will be created.
     * @param template The template of this definition.
     * @param role A comma-separated list of roles. If present, the definition
     * will be rendered only if the current user belongs to one of the roles.
     * @param extendsParam The definition name that this definition extends.
     * @param preparer The preparer to use to invoke before the definition is rendered.
     * @param request TODO
     * @since 2.2.0
     */
    public void execute(MutableTilesContainer container,
            ArrayStack<Object> composeStack, String name, String template,
            String role, String extendsParam, String preparer,
            Request request) {
        Definition definition = createDefinition(name, template, role,
                extendsParam, preparer);
        registerDefinition(definition, container, composeStack, request);
    }

    /**
     * Creates the definition to store.
     *
     * @param name The name of the definition to create. If not specified, an anonymous definition will be created.
     * @param template The template of this definition.
     * @param role A comma-separated list of roles. If present, the definition
     * will be rendered only if the current user belongs to one of the roles.
     * @param extendsParam The definition name that this definition extends.
     * @param preparer The preparer to use to invoke before the definition is rendered.
     * @return The created definition.
     */
    private Definition createDefinition(String name, String template,
            String role, String extendsParam, String preparer) {
        Definition definition = new Definition();
        definition.setName(name);
        Attribute templateAttribute = Attribute
                .createTemplateAttribute(template);
        templateAttribute.setRole(role);
        definition.setTemplateAttribute(templateAttribute);
        definition.setExtends(extendsParam);
        definition.setPreparer(preparer);
        return definition;
    }

    /**
     * Registers a definition in the container.
     *
     * @param definition The definition to register.
     * @param container The container into which the definition will be registered.
     * @param composeStack The compose stack,
     * @param request TODO
     */
    private void registerDefinition(Definition definition,
            MutableTilesContainer container, ArrayStack<Object> composeStack,
            Request request) {
        container.register(definition, request);

        if (composeStack.isEmpty()) {
            return;
        }

        Object obj = composeStack.peek();
        if (obj instanceof Attribute) {
            Attribute attribute = (Attribute) obj;
            attribute.setValue(definition.getName());
            if (attribute.getRenderer() == null) {
                attribute.setRenderer("definition");
            }
        }
    }
}
