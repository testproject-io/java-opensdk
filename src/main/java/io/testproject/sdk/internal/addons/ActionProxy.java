/*
 * Copyright 2020 TestProject LTD. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.testproject.sdk.internal.addons;

/**
 * Base class for all Action proxies.
 */
public class ActionProxy {

    /**
     * Metadata about the original addon.
     */
    private transient ProxyDescriptor descriptor;

    /**
     * Getter for {@link #descriptor} field.
     *
     * @return value of {@link #descriptor} field
     */
    public ProxyDescriptor getDescriptor() {
        return this.descriptor;
    }

    /**
     * Setter for <em>descriptor</em> field.
     *
     * @param descriptor Descriptor to set.
     */
    public void setDescriptor(final ProxyDescriptor descriptor) {
        this.descriptor = descriptor;
    }
}
