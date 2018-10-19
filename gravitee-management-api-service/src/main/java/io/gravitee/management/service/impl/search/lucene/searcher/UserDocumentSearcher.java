package io.gravitee.management.service.impl.search.lucene.searcher;

import io.gravitee.management.model.UserEntity;
import io.gravitee.management.model.search.Indexable;
import io.gravitee.management.service.impl.search.SearchResult;
import io.gravitee.repository.exceptions.TechnicalException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.*;
import org.springframework.stereotype.Component;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@Component
public class UserDocumentSearcher extends AbstractDocumentSearcher {

    protected final static String FIELD_TYPE_VALUE = "user";

    @Override
    public SearchResult search(io.gravitee.management.service.search.query.Query query) throws TechnicalException {
        QueryParser parser = new MultiFieldQueryParser(new String[]{
                "firstname",
                "lastname",
                "email"
        }, analyzer);
        parser.setFuzzyMinSim(0.6f);
        parser.setAllowLeadingWildcard(true);

        try {
            Query parse = parser.parse(QueryParserBase.escape(query.getQuery()));

            BooleanQuery.Builder userQuery = new BooleanQuery.Builder();
            BooleanQuery.Builder userFieldsQuery = new BooleanQuery.Builder();

            userFieldsQuery.add(parse, BooleanClause.Occur.SHOULD);
            userFieldsQuery.add(new WildcardQuery(new Term("firstname", '*' + query.getQuery() + '*')), BooleanClause.Occur.SHOULD);
            userFieldsQuery.add(new WildcardQuery(new Term("lastname", '*' + query.getQuery() + '*')), BooleanClause.Occur.SHOULD);
            userFieldsQuery.add(new WildcardQuery(new Term("email", '*' + query.getQuery() + '*')), BooleanClause.Occur.SHOULD);

            userQuery.add(userFieldsQuery.build(), BooleanClause.Occur.MUST);
            userQuery.add(new TermQuery(new Term(FIELD_TYPE, FIELD_TYPE_VALUE)), BooleanClause.Occur.MUST);

            return search(userQuery.build(), query.getPage());
        } catch (ParseException pe) {
            logger.error("Invalid query to search for user documents", pe);
            throw new TechnicalException("Invalid query to search for user documents", pe);
        }
    }

    @Override
    public boolean handle(Class<? extends Indexable> source) {
        return source.isAssignableFrom(UserEntity.class);
    }
}
