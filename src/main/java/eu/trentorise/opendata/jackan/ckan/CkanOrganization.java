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
package eu.trentorise.opendata.jackan.ckan;

/**
 * Class to explicitly model a Ckan organization, which is <i> not </i> a group,
 * although is has the same attributes.
 *
 * @see CkanGroupStructure
 * @author David Leoni
 */
public class CkanOrganization extends CkanGroupStructure {

    public CkanOrganization() {
        super();
        setOrganization(true);
    }

    /**
     * Constructor for an organization with the minimal amount of parameters needed to be created on the server.
     * @param name Name in the url, lowercased and without spaces. i.e. management-of-territory     
     */
    public CkanOrganization(String name) {
        this.setName(name);
    }
}
