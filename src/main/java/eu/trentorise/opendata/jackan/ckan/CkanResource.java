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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import static eu.trentorise.opendata.commons.validation.Preconditions.checkNotEmpty;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * 
 * A Resource describes with metadata a physical file. Resources are part of {@link CkanDataset}.
 * Class initializes almost nothing to fully preserve all we get from ckan.
 *
 * In DCAT terminology, a Ckan Resource is a DCAT Distribution.
 * @author David Leoni
 */
public class CkanResource {

    @Nullable
    private String cacheLastUpdated;

    @Nullable
    private String cacheUrl;

    /*
     not in rest api 
     
     private String datasetName; // laghi-monitorati-trento
     private String datasetTitle; // Laghi monitorati Trento
     */
    private Date cacheUrlUpdated;

    private Date created;

    private String description;

    private List<CkanPair> extras;

    private String format;

    private String hash;

    private String id;

    @Nullable
    private Date lastModified;

    @Nullable
    private String mimetype;

    @Nullable
    private String mimetypeInner;

    private String name;

    @Nullable
    private String owner;

    private int position;

    private String resourceGroupId;

    private String resourceType;

    private String revisionId;

    @Nullable
    private String revisionTimestamp;

    @Nullable
    private String size;

    private CkanState state;

    private TrackingSummary trackingSummary;

    private String url;

    @Nullable
    private String urlType;

    @Nullable
    private Date webstoreLastUpdated;

    @Nullable
    private String webstoreUrl;

    /**
     * The dataset this resource belongs to. Not present when getting resources
     * but needed when uploading them.
     */
    @Nullable
    private String packageId;

    /**
     * Custom CKAN instances might sometimes gift us with properties that don't
     * end up in extras as they should. They will end up here.
     */
    private Map<String, Object> others;

    /**
     * Returns the dataset this resource belongs to. Not present when getting
     * resources but needed when uploading them.
     */
    @Nullable
    public String getPackageId() {
        return packageId;
    }

    /**
     * Sets the dataset id the resource belongs to. Not present when getting
     * resources but needed when uploading them.
     *
     * @param packageId the dataset this resource belongs to.
     */
    public void setPackageId(@Nullable String packageId) {
        this.packageId = packageId;
    }

    public CkanResource() {
        others = new HashMap();
    }

    /**
     * Constructor with the minimal list of required items to successfully
     * create a resource on the server.
     *
     * @param format i.e. file format in capital letters, i.e. "CSV"
     * @param name resource name, i.e. "My Cool resource"
     * @param url the Url to the pyhsical file, i.e.
     * http://dati.trentino.it/storage/f/2013-05-09T140831/TRENTO_Laghi_monitorati_UTM.csv
     * @param description
     * @param packageId id of the dataset that contains the resource
     */
    public CkanResource(String format,
            String name,
            String url,
            String description,
            String packageId) {
        this();        
        this.format = format;
        this.name = name;
        this.url = url;
        this.description = description;
        this.packageId = packageId;
    }

    /**
     * CKAN instances might have
     * <a href="http://docs.ckan.org/en/latest/extensions/adding-custom-fields.html">
     * custom data schemas</a> that force presence of custom properties among
     * 'regular' ones given by {@link #getExtras()}. In this case, they go to 
     * 'others' field.
     */
    @JsonAnyGetter
    public Map<String, Object> getOthers() {
        return others;
    }

    /**
     * See {@link #getOthers()}
     */
    @JsonAnySetter
    public void setOthers(String name, Object value) {
        others.put(name, value);
    }

    /**
     * Should be a Date
     */
    @Nullable
    public String getCacheLastUpdated() {
        return cacheLastUpdated;
    }

    /**
     * Should be a Date
     */
    public void setCacheLastUpdated(@Nullable String cacheLastUpdated) {
        this.cacheLastUpdated = cacheLastUpdated;
    }

    /**
     * God only knows what this is
     */
    @Nullable
    public String getCacheUrl() {
        return cacheUrl;
    }

    /**
     * God only knows what this is
     */
    public void setCacheUrl(@Nullable String cacheUrl) {
        this.cacheUrl = cacheUrl;
    }

    /**
     * Ckan always refers to UTC timezone
     */
    @Nullable
    public Date getCacheUrlUpdated() {
        return cacheUrlUpdated;
    }

    /**
     * Ckan always refers to UTC timezone
     */
    public void setCacheUrlUpdated(@Nullable Date cacheUrlUpdated) {
        this.cacheUrlUpdated = cacheUrlUpdated;
    }

    /**
     * In JSON is something like this: i.e. "2013-05-09T14:08:32.666477" . Ckan
     * always refers to UTC timezone
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Ckan always refers to UTC timezone
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public Map<String, String> getExtrasAsHashMap() {
        HashMap<String, String> hm = new HashMap();
        for (CkanPair cp : extras) {
            hm.put(cp.getKey(), cp.getValue());
        }
        return hm;
    }

    /**
     * Regular place where to put custom metadata. See also
     * {@link #getOthers()}.
     */    
    public List<CkanPair> getExtras() {
        return extras;
    }

    /**
     * See {@link #getExtras()}
     */    
    public void setExtras(List<CkanPair> extras) {
        this.extras = extras;
    }

    /**
     * In Ckan 1.8 was lowercase, 2.2a seems capitalcase.
     */
    public String getFormat() {
        return format;
    }

    /**
     * In Ckan 1.8 was lowercase, 2.2a seems capitalcase.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Sometimes for dati.trentino.it can be the empty string
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sometimes for dati.trentino.it can be the empty string
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * Returns the alphanumerical id, i.e.
     * "c4577b8f-5603-4098-917e-da03e8ddf461"
     */
    public String getId() {
        return id;
    }

    /**
     * @param id alphanumerical id, i.e. "c4577b8f-5603-4098-917e-da03e8ddf461"
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Ckan always refers to UTC timezone
     */
    @Nullable
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * Ckan always refers to UTC timezone
     */
    public void setLastModified(@Nullable Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * i.e. text/csv
     */
    @Nullable
    public String getMimetype() {
        return mimetype;
    }

    /**
     * @param mimetype i.e. text/csv
     */
    public void setMimetype(@Nullable String mimetype) {
        this.mimetype = mimetype;
    }

    /**
     * Unknown meaning, as usual. Can be the empty string or null
     */
    @Nullable
    public String getMimetypeInner() {
        return mimetypeInner;
    }

    /**
     * Unknown meaning, as usual. Can be the empty string or null
     */
    public void setMimetypeInner(@Nullable String mimetypeInner) {
        this.mimetypeInner = mimetypeInner;
    }

    /**
     *
     * Human readable name, i.e. "Apple Production 2013 in CSV format"
     *
     *
     * Notice we found name null in data.gov.uk datasets... i.e.
     * <a href="http://data.gov.uk/api/3/action/resource_show?id=77d2dba8-d0d9-49ef-9fd2-37a4a8bc5a17" target="_blank">
     * unclaimed-estates-list </a>, taken
     * <a href="http://data.gov.uk/api/3/action/package_search?rows=20&start=0" target="_blank">from
     * this dataset search</a> (They use description field instead)
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Human readable name, i.e. "Apple Production 2013 in CSV format". For
     * Nullable explanation see {@link #getName()}
     */
    public void setName(@Nullable String name) {
        this.name = name;
    }

    /**
     * Username of the owner
     */
    @Nullable
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner Username of the owner
     */
    public void setOwner(@Nullable String owner) {
        this.owner = owner;
    }

    /**
     * Position inside the dataset?
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position Position inside the dataset?
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * todo - What the hell is this? alphanumerical id, i.e.
     * "fd6375cd-1d6a-41e8-8e10-460a11e2308e"
     */
    public String getResourceGroupId() {
        return resourceGroupId;
    }

    /**
     * todo - What the hell is this? alphanumerical id, i.e.
     * "fd6375cd-1d6a-41e8-8e10-460a11e2308e"
     */
    public void setResourceGroupId(String resourceGroupId) {
        this.resourceGroupId = resourceGroupId;
    }

    /**
     * So far, found: "api", "file", "file.upload"
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * So far, found: "api", "file", "file.upload"
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * alphanumerical id, i.e. 0c949f17-d123-4379-8536-cfcf25b3b0e9
     */
    public String getRevisionId() {
        return revisionId;
    }

    /**
     * alphanumerical id, i.e. 0c949f17-d123-4379-8536-cfcf25b3b0e9
     */
    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    /**
     * Should be a date
     */
    @Nullable
    public String getRevisionTimestamp() {
        return revisionTimestamp;
    }

    /**
     * Should be a date
     */
    public void setRevisionTimestamp(@Nullable String revisionTimestamp) {
        this.revisionTimestamp = revisionTimestamp;
    }

    /**
     * @return File size in bytes, if calculated by ckan for files in storage,
     * like i.e. "242344". Otherwise it can be anything a human can insert.
     */
    @Nullable
    public String getSize() {
        return size;
    }

    /**
     * @param size File size in bytes, if calculated by ckan for files in
     * storage, like i.e. "242344". Otherwise it can be anything a human can
     * insert.
     */
    public void setSize(@Nullable String size) {
        this.size = size;
    }

    public CkanState getState() {
        return state;
    }

    public void setState(CkanState state) {
        this.state = state;
    }

    public TrackingSummary getTrackingSummary() {
        return trackingSummary;
    }

    public void setTrackingSummary(TrackingSummary trackingSummary) {
        this.trackingSummary = trackingSummary;
    }

    /**
     * Returns the Url to the pyhsical file, i.e.
     * http://dati.trentino.it/storage/f/2013-05-09T140831/TRENTO_Laghi_monitorati_UTM.csv
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the Url to the pyhsical file, i.e.
     * http://dati.trentino.it/storage/f/2013-05-09T140831/TRENTO_Laghi_monitorati_UTM.csv
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * todo - Don't know what it is
     */
    @Nullable
    public String getUrlType() {
        return urlType;
    }

    /**
     * todo - Don't know what it is
     */
    public void setUrlType(@Nullable String urlType) {
        this.urlType = urlType;
    }

    /**
     * Ckan always refers to UTC timezone
     */
    @Nullable
    public Date getWebstoreLastUpdated() {
        return webstoreLastUpdated;
    }

    /**
     * Ckan always refers to UTC timezone
     */
    public void setWebstoreLastUpdated(@Nullable Date webstoreLastUpdated) {
        this.webstoreLastUpdated = webstoreLastUpdated;
    }

    /**
     * Found "active" as value. Maybe it is a CkanState?
     */
    @Nullable
    public String getWebstoreUrl() {
        return webstoreUrl;
    }

    /**
     * @param webstoreUrl Found "active" as value. Maybe it is a CkanState?
     */
    public void setWebstoreUrl(@Nullable String webstoreUrl) {
        this.webstoreUrl = webstoreUrl;
    }

}
