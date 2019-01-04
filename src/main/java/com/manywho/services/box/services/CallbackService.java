package com.manywho.services.box.services;

import com.box.sdk.BoxFile;
import com.box.sdk.Metadata;
import com.box.sdk.MetadataTemplate;
import com.manywho.services.box.entities.ExecutionFlowMetadata;
import com.manywho.services.box.utilities.ParseUrlUtility;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CallbackService {

    @Inject
    public CallbackService(){
    }

    public void overwriteNullValuesWithDefaultOptions(List<ExecutionFlowMetadata> fileMetadata, Iterable<MetadataTemplate> defaultValues) throws URISyntaxException {
        String templateFlowKey = "";
        String defaultTriggerId;
        String defaultUri;

        for (MetadataTemplate template: defaultValues) {
            defaultUri = "";
            defaultTriggerId = "";
            for (MetadataTemplate.Field field: template.getFields()) {

                if (Objects.equals(field.getDisplayName(), "ManyWho Flow Uri") && field.getOptions() != null) {
                    defaultUri = field.getOptions().get(0);
                    templateFlowKey = template.getTemplateKey();
                }

                if (Objects.equals(field.getDisplayName(), "ManyWho Flow Trigger") && field.getOptions() != null) {
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
                executionFlowMetadata.setFlowId(ParseUrlUtility.getFlowId(metadata.get("/manywhoFlowUri")));
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
