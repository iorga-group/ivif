package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.views.QueryParser.ParsedQuery;
import com.iorga.ivif.ja.tag.views.QueryParser.QueryParameter;
import com.iorga.ivif.tag.AbstractTarget;
import com.iorga.ivif.tag.bean.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.iorga.ivif.ja.tag.entities.EntityTargetFile.EntityTargetFileId;

public class QueryModel extends AbstractTarget<String, JAGeneratorContext> {
    private final static Logger LOG = LoggerFactory.getLogger(QueryModel.class);

    private final Query element;
    private final EntityTargetFileId baseEntityId;
    private final Object waiter;
    private String queryDslCode;
    private List<QueryParameter> parameters;


    public QueryModel(String id, Query element, EntityTargetFileId baseEntityId, Object waiter) {
        super(id);
        this.element = element;
        this.baseEntityId = baseEntityId;
        this.waiter = waiter;
    }


    @Override
    public void prepare(JAGeneratorContext context) throws Exception {
        super.prepare(context);

        parameters = new ArrayList<>();
        queryDslCode = null;

        if (element != null) {
            // Parsing parameter values
            final ParsedQuery parsedQuery = QueryParser.parse(element, baseEntityId, waiter, context);

            if (parsedQuery != null) {
                queryDslCode = parsedQuery.getQueryDslCode();
                parameters = parsedQuery.getQueryParameters();
            }

        }
    }


        // TODO Add this action parameters to the BaseWS => create a context.waitForTargetFileToBePrepared(targetFile, waiter) (the waiter will have a source, which will be this here
        // TODO réfléchir sur les arbres de dépendances de waiters (parts & targetFile), dans le but de reconstituer l'arbre des modifs à appliquer si un fichier source change
        // TODO Add this action query to the BaseWS->BaseService.search

        // TODO declare this handler


    public String getQueryDslCode() {
        return queryDslCode;
    }

    public List<QueryParameter> getParameters() {
        return parameters;
    }
}
