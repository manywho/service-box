package com.manywho.services.box.services;

import com.box.sdk.BoxFile;
import com.box.sdk.BoxMetadataTemplate;
import com.box.sdk.Metadata;
import com.box.sdk.MetadataField;
import com.manywho.services.box.entities.ExecutionFlowMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CallbackService {
    public void overwriteNullValuesWithDefaultOptions(List<ExecutionFlowMetadata> fileMetadata, List<BoxMetadataTemplate.Info> defaultValues) {
        String templateFlowKey = "";
        String defaultFlowVersionId;
        String defaultFlowId;

        for (BoxMetadataTemplate.Info template: defaultValues) {
            defaultFlowVersionId = "";
            defaultFlowId = "";
            for (MetadataField field: template.getFields()) {

                if (Objects.equals(field.getDisplayName(), "flow-id")) {
                    defaultFlowId = field.getOptions().get(0);
                    templateFlowKey = template.getTemplateKey();
                }

                if (Objects.equals(field.getDisplayName(), "flow-version-id")) {
                    defaultFlowVersionId = field.getOptions().get(0);
                    templateFlowKey = template.getTemplateKey();
                }
            }

            if(!defaultFlowId.isEmpty() && !defaultFlowVersionId.isEmpty()) {
                populateEmptyValuesWithDefaultTemplate(fileMetadata, templateFlowKey, defaultFlowId, defaultFlowVersionId);
            }
        }
    }

    public String getEnterpriseIdFromMetadata(List<ExecutionFlowMetadata> executionFlowMetadatas){
        for (ExecutionFlowMetadata executionMetadata: executionFlowMetadatas) {
            if(executionMetadata.getEnterpriseId()!= null) return executionMetadata.getEnterpriseId();
        }

        return null;
    }

    public List<ExecutionFlowMetadata> getAllPossibleExecutionFlowMetadata(BoxFile boxFile) {
        List<ExecutionFlowMetadata> fileMetadataList = new ArrayList<>();

        for (Metadata metadata:boxFile.getAllMetadata()) {
            ExecutionFlowMetadata executionFlowMetadata = new ExecutionFlowMetadata(metadata.getTemplateName());

            if(metadata.get("/flowid") != null) {
                executionFlowMetadata.setFlowId(metadata.get("/flowid"));
                executionFlowMetadata.setFlowId(metadata.get("/flowversionid"));
            }

            if(metadata.getScope()!= null) {
                executionFlowMetadata.setEnterpriseId(metadata.getScope().substring(11));
            }

            fileMetadataList.add(executionFlowMetadata);
        }

        return fileMetadataList;
    }

    public void populateEmptyValuesWithDefaultTemplate(List<ExecutionFlowMetadata> fileMetadata, String keyTemplate, String flowId, String flowVersionId) {
        for (ExecutionFlowMetadata metadata:fileMetadata) {
            if(Objects.equals(metadata.getKeyTemplate(), keyTemplate)) {
                if(metadata.getFlowId() == null) {
                    metadata.setFlowId(flowId);
                }

                if(metadata.getFlowVersionId() == null) {
                    metadata.setFlowVersionId(flowVersionId);
                }
            }
        }
    }
}
