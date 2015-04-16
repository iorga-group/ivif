package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.SortingType;
import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.views.QueryParser.ParsedQuery;
import com.iorga.ivif.tag.bean.Query;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QueryParserTest {
    @Test
    public void complexParsingTest() throws Exception {
        // mock creation
        final JAGeneratorContext context = mock(JAGeneratorContext.class);

        final Query query = new Query();
        query.setWhere("$record.test = :param AND $record.test2.field IN (:param2) OR $record.test3 = 'label' OR $record.test4 > 5 AND $record.test4 IS NULL AND $record.test5 IS NOT NULL");
        final ParsedQuery parsedQuery = QueryParser.parse(query, null, this, context);
        assertThat(parsedQuery.getQueryParameters()).hasSize(2);
        assertThat(parsedQuery.getQueryDslCode()).isEqualTo("$record.test.eq(parameters.param).and($record.test2.field.in(parameters.param2)).or($record.test3.eq(\"label\")).or($record.test4.gt(5).and($record.test4.isNull()).and($record.test5.isNotNull()))");
    }

    @Test
    public void constantParsingTest() throws Exception {
        // mock creation
        final JAGeneratorContext context = mock(JAGeneratorContext.class);

        final Query query = new Query();
        query.setWhere("'test' = :param");
        final ParsedQuery parsedQuery = QueryParser.parse(query, null, this, context);
        assertThat(parsedQuery.getQueryParameters()).hasSize(1);
        assertThat(parsedQuery.getQueryDslCode()).isEqualTo("${util.useClass(\"com.mysema.query.support.Expressions\")}.stringTemplate(\"{0}\", \"test\").eq(parameters.param)");
    }

    @Test
    public void orderBysTest() throws Exception {
        // mock creation
        final JAGeneratorContext context = mock(JAGeneratorContext.class);

        final Query query = new Query();
        query.setWhere("$record.field1 = $record.field2");
        query.setDefaultOrderBy("$record.field3 ASC, $record.field DESC, $record.field4");
        final ParsedQuery parsedQuery = QueryParser.parse(query, null, this, context);
        assertThat(parsedQuery.getDefaultOrderBy()).hasSize(3);
        assertThat(parsedQuery.getDefaultOrderBy().get(2).direction).isEqualTo(SortingType.ASCENDING);
    }

    @Test
    public void notEqualTest() throws Exception {
        // mock creation
        final JAGeneratorContext context = mock(JAGeneratorContext.class);

        final Query query = new Query();
        query.setWhere("$record.test != 0");
        final ParsedQuery parsedQuery = QueryParser.parse(query, null, this, context);
        assertThat(parsedQuery.getQueryDslCode()).isEqualTo("$record.test.ne(0)");
    }
}
