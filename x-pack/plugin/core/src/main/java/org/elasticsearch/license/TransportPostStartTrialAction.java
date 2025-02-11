/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */
package org.elasticsearch.license;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.master.TransportMasterNodeAction;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.block.ClusterBlockException;
import org.elasticsearch.cluster.block.ClusterBlockLevel;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

public class TransportPostStartTrialAction extends TransportMasterNodeAction<PostStartTrialRequest, PostStartTrialResponse> {

    private final ClusterStateLicenseService clusterStateLicenseService;

    @Inject
    public TransportPostStartTrialAction(
        TransportService transportService,
        ClusterService clusterService,
        ClusterStateLicenseService clusterStateLicenseService,
        ThreadPool threadPool,
        ActionFilters actionFilters,
        IndexNameExpressionResolver indexNameExpressionResolver
    ) {
        super(
            PostStartTrialAction.NAME,
            transportService,
            clusterService,
            threadPool,
            actionFilters,
            PostStartTrialRequest::new,
            indexNameExpressionResolver,
            PostStartTrialResponse::new,
            ThreadPool.Names.SAME
        );
        this.clusterStateLicenseService = clusterStateLicenseService;
    }

    @Override
    protected void masterOperation(
        Task task,
        PostStartTrialRequest request,
        ClusterState state,
        ActionListener<PostStartTrialResponse> listener
    ) throws Exception {
        clusterStateLicenseService.startTrialLicense(request, listener);
    }

    @Override
    protected ClusterBlockException checkBlock(PostStartTrialRequest request, ClusterState state) {
        return state.blocks().globalBlockedException(ClusterBlockLevel.METADATA_WRITE);
    }
}
