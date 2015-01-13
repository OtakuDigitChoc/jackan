/* 
 * Copyright 2015 Trento Rise  (trentorise.eu) 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.trentorise.opendata.jackan;

import eu.trentorise.opendata.jackan.ckan.CkanClient;

/**
 *
 * @author David Leoni
 */
public class JackanException extends RuntimeException {

    public JackanException(String msg) {
        super("Jackan: " + msg);
    }

    public JackanException(String msg, Throwable ex) {
        super("Jackan: " + msg, ex);
    }

    public JackanException(String msg, CkanClient client) {
        super("Jackan: " + msg + "\nClient parameters: " + client.toString());
    }

    public JackanException(String msg, CkanClient client, Throwable ex) {
        super("Jackan: " + msg + "\nClient parameters: " + client.toString(), ex);
    }

}
