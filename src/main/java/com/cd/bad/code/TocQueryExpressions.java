package com.cd.bad.code;

import java.util.HashMap;
import java.util.Map;

public class TocQueryExpressions implements XPathProvider {
    private static final String FOLDER_KEY = "fk";
    private static final String FOLDER_TYPE = "ft";
    private static final String FOLDER_TYPE_AS_HISTORY = "fth";
    private static final String DOC_KEY = "dk";
    private static final String DOC_TYPE = "dt";
    private static final String DOC_TYPE_AS_HISTORY = "dth";
    private static final String DOC_TRNUM = "dtrn";

    private static final Map<String, String> XPATH_EXPRESSIONS_BY_QUERY_KEY;

    static {
        XPATH_EXPRESSIONS_BY_QUERY_KEY = new HashMap<String, String>();
        XPATH_EXPRESSIONS_BY_QUERY_KEY.put(FOLDER_KEY, "folder[@key");
        XPATH_EXPRESSIONS_BY_QUERY_KEY.put(FOLDER_TYPE, "folder[@type");
        XPATH_EXPRESSIONS_BY_QUERY_KEY.put(FOLDER_TYPE_AS_HISTORY, "folder[@type='history'");
        XPATH_EXPRESSIONS_BY_QUERY_KEY.put(DOC_KEY, "doc[@key");
        XPATH_EXPRESSIONS_BY_QUERY_KEY.put(DOC_TYPE, "doc[@type");
        XPATH_EXPRESSIONS_BY_QUERY_KEY.put(DOC_TYPE_AS_HISTORY, "doc[@type='history'");
        XPATH_EXPRESSIONS_BY_QUERY_KEY.put(DOC_TRNUM, "doc[@trnum");
    }

    /**
     * post string looks like :
     * <p>
     * "fk:LOETR_dtrn:TR12-118_fth_dth" it represents the inner doc element:
     * <folder key="LOETR" type="loetr"
     * title="List of Effective TRs" file="loetr.html"> <doc type="tr"
     * trnum="TR12-118" trdate="May 07/2012"
     * title="[TR12-118] TASK AMM12-31-00-660-806 - Inspection and Removal of De-Hydrated Anti-Icing Fluid inside the Flight Control Surfaces"
     * file="TR12-118.pdf" refloc="AMM12-31-00-660-806"> <folder type="history"
     * title="History of AMM12-31-00-660-806"> <doc
     * title="TASK 12-31-00-660-806 - Inspection and Removal of De-Hydrated Anti-Icing Fluid inside the Flight Control Surfaces"
     * file="AMM12-31-00-660-806.pdf" type="history" refloc="AMM12-31-00-660-806"/>
     * </folder>
     * <p>
     * the xpath string should be:
     * <p>
     * folder[@key="LOETR"]/doc[@trnum="TR12-118"]/folder[@type="history"]/doc[@type
     * ="history"]
     * <p>
     * <p>
     * the String : "fk:AMM24_fk:AMM24-FM_dk:CTOC-24" it represents the inner doc with attribute file="CTOC-24.pdf"
     * <p>
     * the string : "fk:AMM24_fk:AMM24-00-00_fk:AMM24-00-00-02_dk:AMM24-00-00-700-801" represents
     * <folder key="AMM24" title="CH 24 - Electrical Power"> <folder key="AMM24-FM"
     * title="Front Matter"> <doc key="CTOC-24" title="Table of Contents"
     * file="CTOC-24.pdf"/> </folder> <folder key="AMM24-00-00"
     * title="24-00-00 - General"> <folder key="AMM24-00-00-02"
     * title="General - Maintenance Practices"> <doc key="AMM24-00-00-700-801"
     * title="TASK 24-00-00-700-801 - AC Power, DC Power and Battery Maintenance Practice Recommendations"
     * file="AMM24-00-00-700-801.pdf"/>
     * <p>
     * it can be even optimised as: "fk:AMM24_fk:00-00_fk:02_dk:AMM24-00-00-700-801"
     * <p>
     * if the inner key fully includes the previous key, omit it. Otherwise use full string the xpath string should be:
     * folder[@key="AMM24"]/folder[@key="AMM24-00-00"]/folder[@key="AMM24-00-00-02"]/doc[@key="AMM24-00-00-700-801"]
     * <p>
     * if shortXPath is ?? which means the query based on the root of the document
     */
    public String xPathFrom(String query) {
        String xPathExpression = "//";

        if (query.equals("")) {
            xPathExpression = "//toc";
        }

        int start = 0;
        int underscorePosition = query.indexOf("_", start);

        while (underscorePosition > -1) {
            String subquery = query.substring(start, underscorePosition);
            start = underscorePosition + 1;
            xPathExpression = withUnfinishedFilledValue(xPathExpression, subquery);
            underscorePosition = query.indexOf("_", start);
        }

        return withFinishedFilledValue(xPathExpression, query.substring(start));
    }

    private static String withFinishedFilledValue(String xPathExpression, String subquery) {
        return withFilledValue(xPathExpression, subquery, "']");
    }

    private static String withUnfinishedFilledValue(String xPathExpression, String subquery) {
        return withFilledValue(xPathExpression, subquery, "']/");
    }

    private static String withFilledValue(String xPathExpression, String subquery, String closure) {
        int keyValueSeparatorIndex = subquery.indexOf(":");
        if (keyValueSeparatorIndex > 0) {
            String keyContent = subquery.substring(0, keyValueSeparatorIndex);
            String valueContent = subquery.substring(keyValueSeparatorIndex + 1);
            xPathExpression = xPathExpression.concat(XPATH_EXPRESSIONS_BY_QUERY_KEY.get(keyContent));
            xPathExpression = xPathExpression.concat("='").concat(valueContent).concat(closure);
        }
        return xPathExpression;
    }
}
