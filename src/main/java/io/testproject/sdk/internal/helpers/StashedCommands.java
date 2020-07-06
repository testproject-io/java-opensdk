/*
 * Copyright (c) 2020 TestProject LTD. and/or its affiliates
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

package io.testproject.sdk.internal.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Wrapper around stashed commands HashMap with extended functionality.
 */
class StashedCommands {

    /**
     * Stashed commands map.
     */
    private final HashMap<Integer, StashedCommand> map = new HashMap<>();

    /**
     * Add a command.
     * @param stashedCommand Command to add.
     */
    public void add(final StashedCommand stashedCommand) {
        map.put(stashedCommand.getCommand().hashCode(), stashedCommand);
    }

    /**
     * List stashed commands.
     * @return Stashed commands list.
     */
    public List<StashedCommand> list() {
        return new ArrayList<>(map.values());
    }

    /**
     * Get a specific commands using its hash.
     * @param hash {@link StashedCommand} hash.
     * @return Specific {@link StashedCommand}
     */
    public StashedCommand get(final int hash) {
        return map.get(hash);
    }

    /**
     * Clear stashed commands.
     */
    public void clear() {
        map.clear();
    }
}
