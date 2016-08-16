package com.manywho.services.box.services;

import com.box.sdk.BoxFile;
import com.box.sdk.BoxMetadataTemplate;
import com.box.sdk.Metadata;
import com.box.sdk.MetadataField;
import com.manywho.services.box.entities.ExecutionFlowMetadata;
import com.manywho.services.box.utilities.ParseUrlUtility;
import org.apache.commons.lang3.StringUtils;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CallbackService {
    public void overwriteNullValuesWithDefaultOptions(List<ExecutionFlowMetadata> fileMetadata, List<BoxMetadataTemplate.Info> defaultValues) throws URISyntaxException {
        String templateFlowKey = "";
        String defaultTriggerId;
        String defaultUri;

        for (BoxMetadataTemplate.Info template: defaultValues) {
            defaultUri = "";
            defaultTriggerId = "";
            for (MetadataField field: template.getFields()) {

                if (Objects.equals(field.getDisplayName(), "ManyWho Flow Uri")) {
                    defaultUri = field.getOptions().get(0);
                    templateFlowKey = template.getTemplateKey();
                }

                if (Objects.equals(field.getDisplayName(), "ManyWho Flow Trigger")) {
                    defaultTriggerId = field.getOptions().get(0);
                    templateFlowKey = template.getTemplateKey();
                }
            }

            if(!StringUtils.isEmpty(defaultUri)) {
                populateEmptyValuesWithDefaultTemplate(fileMetadata,
                        templateFlowKey,
                        ParseUrlUtility.getFlowId(defaultUri),
                        ParseUrlUtility.getFlowVersionId(defaultUri),
                        ParseUrlUtility.getTenantId(defaultUri),
                        defaultTriggerId);
            }
        }
    }

    public String getEnterpriseIdFromMetadata(List<ExecutionFlowMetadata> executionFlowMetadatas){
        for (ExecutionFlowMetadata executionMetadata: executionFlowMetadatas) {
            if(executionMetadata.getEnterpriseId()!= null) return executionMetadata.getEnterpriseId();
        }

        return null;
    }

    public List<ExecutionFlowMetadata> getAllPossibleExecutionFlowMetadata(BoxFile boxFile) throws URISyntaxException {
        List<ExecutionFlowMetadata> fileMetadataList = new ArrayList<>();

        for (Metadata metadata:boxFile.getAllMetadata()) {
            ExecutionFlowMetadata executionFlowMetadata = new ExecutionFlowMetadata(metadata.getTemplateName());

            if(metadata.get("/manywhoFlowUri") != null) {
                executionFlowMetadata.setFlowVersionId(ParseUrlUtility.getFlowId(metadata.get("/manywhoFlowUri")));
                executionFlowMetadata.setFlowVersionId(ParseUrlUtility.getFlowVersionId(metadata.get("/manywhoFlowUri")));
                executionFlowMetadata.setTenantId(ParseUrlUtility.getTenantId(metadata.get("/manywhoFlowUri")));

                if(metadata.get("/manywhoFlowTrigger") != null) {
                    executionFlowMetadata.setTrigger(metadata.get("/manywhoFlowTrigger"));
                }
            }

            if(metadata.getScope() != null) {
                executionFlowMetadata.setEnterpriseId(metadata.getScope().substring(11));
            }

            fileMetadataList.add(executionFlowMetadata);
        }

        return fileMetadataList;
    }

    public void populateEmptyValuesWithDefaultTemplate(List<ExecutionFlowMetadata> fileMetadata, String keyTemplate,
                                                       String flowId, String flowVersionId, String defaultTenantId,
                                                       String defaultTriggerId) {

        for (ExecutionFlowMetadata executionFlowMetadata:fileMetadata) {
            if(Objects.equals(executionFlowMetadata.getKeyTemplate(), keyTemplate)) {
                if(StringUtils.isEmpty(executionFlowMetadata.getFlowId())) {
                    executionFlowMetadata.setFlowId(flowId);
                }

                if(StringUtils.isEmpty(executionFlowMetadata.getFlowVersionId())) {
                    executionFlowMetadata.setFlowVersionId(flowVersionId);
                }

                if(StringUtils.isEmpty(executionFlowMetadata.getTenantId())) {
                    executionFlowMetadata.setTenantId(defaultTenantId);
                }

                if(StringUtils.isEmpty(executionFlowMetadata.getTrigger())) {
                    executionFlowMetadata.setTrigger(defaultTriggerId);
                }
            }
        }
    }
}
