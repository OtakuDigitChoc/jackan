/*
 * Copyright 2015 Trento Rise.
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

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Strings;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanDatasetBase;
import eu.trentorise.opendata.jackan.model.CkanOrganization;
import eu.trentorise.opendata.jackan.model.CkanResource;
import eu.trentorise.opendata.jackan.model.CkanResourceBase;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpHost;

/**
 * This client performs additional checks when writing to CKAN to ensure written
 * content is correct. For this reason it might do additional calls and results
 * of validation might be different from default Ckan ones. But if Ckan actually
 * performed all the checks it should do there wouldn't be any need of this
 * class as well..
 *
 * @author David Leoni
 */
public class CheckedCkanClient extends CkanClient {

    public CheckedCkanClient(String url) {
        super(url);
    }

    public CheckedCkanClient(String URL, String token) {
        super(URL, token);
    }

    public CheckedCkanClient(String URL, String token, HttpHost proxy) {
        super(URL, token, proxy);
    }

    
    @Override
    public synchronized CkanOrganization createOrganization(CkanOrganization org) {
        return super.createOrganization(org);
    }

    @Override
    public synchronized CkanDataset createDataset(CkanDatasetBase dataset) {
        return super.createDataset(dataset);
    }

    @Override
    public synchronized CkanResource createResource(CkanResourceBase resource) {
        checkNotNull(resource, "Need a valid resource!");

        if (getCkanToken() == null) {
            throw new CkanException("Tried to create resource" + resource.getName() + ", but ckan token was not set!", this);
        }
        
        if (!Strings.isNullOrEmpty(resource.getId())){
            try {
                UUID.fromString(resource.getId());
            } catch (IllegalArgumentException ex){
                throw new CkanException("Jackson validation failed! Tried to create resource with invalid UUID: '" + resource.getId() + "'", this, ex);
            }            
            try {
                CkanResource dupRes = getResource(resource.getId());
                throw new CkanException("Jackan validation failed! Tried to create resource with existing id! " + resource.getId(), this);
            } catch (CkanNotFoundException ex){
                
            }
        }
        
        try {
            new URL(resource.getUrl());
        }
        catch (MalformedURLException ex) {
            throw new CkanException("Jackan validation failed! Tried to create resource with ill-formed url:" + resource.getUrl(), this);
        }
        
        
        return super.createResource(resource);
    }

}
