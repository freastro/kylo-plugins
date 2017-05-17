/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thinkbiganalytics.metadata;

import com.thinkbiganalytics.metadata.api.MetadataAccess;
import com.thinkbiganalytics.metadata.api.PostMetadataConfigAction;
import com.thinkbiganalytics.metadata.modeshape.JcrMetadataAccess;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

@Service
@Order(PostMetadataConfigAction.LATE_ORDER + 100)
public class FeedDeleteAction implements PostMetadataConfigAction {

    private static final String FEED_ID = "YOUR-FEED-ID-HERE";

    @Inject
    private MetadataAccess metadataAccess;

    @Override
    public void run() {
        metadataAccess.commit(() -> {
            final Node feedNode = JcrMetadataAccess.getActiveSession().getNodeByIdentifier(FEED_ID);
            final PropertyIterator references = feedNode.getReferences();
            while (references.hasNext()) {
                final Property property = references.nextProperty();
                if (property.isMultiple()) {
                    final Value[] values = Arrays.stream(property.getValues())
                        .filter(value -> {
                            try {
                                return !value.getString().equals(FEED_ID);
                            } catch (final RepositoryException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toArray(Value[]::new);
                    property.setValue(values);
                }
            }
            feedNode.remove();
        }, MetadataAccess.SERVICE);
    }
}
