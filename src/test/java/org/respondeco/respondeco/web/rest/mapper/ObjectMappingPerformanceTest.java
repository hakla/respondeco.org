package org.respondeco.respondeco.web.rest.mapper;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.web.rest.mapper.parser.ExpressionParsingException;
import org.respondeco.respondeco.web.rest.mapper.parser.FieldExpressionParser;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

/**
 * Created by clemens on 25/03/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ObjectMappingPerformanceTest extends AbstractBenchmark {

    private static final String PARSER_TEST_EXPRESSION =
        "aaa,bbb(ccc,-ddd),eee(fff(ggg(hhh(iii(jjj))))),kkk(lll(mmm))";
    private static final String PROJECT_MAPPING_EXRESSION =
        "+concrete,manager(-id,firstName),organization(owner(firstName))";

    private FieldExpressionParser parserTestParser;
    private FieldExpressionParser.ParserListener listenerMock;

    private FieldExpressionParser listenerTestParser;
    private FieldExpressionParser.ParserListener listener;

    private Project project;
    private ObjectMapper mapper;

    @Inject
    private ObjectMapperFactory projectMapperFactory;

    @Before
    public void setUp() throws Exception {
        parserTestParser = new FieldExpressionParser(Integer.MAX_VALUE, 0, PARSER_TEST_EXPRESSION);
        listenerMock = new FieldExpressionParser.ParserListener() {
            @Override
            public void onNegatedExpression(String name) throws ExpressionParsingException { }
            @Override
            public void onSimpleExpression(String name) throws ExpressionParsingException { }
            @Override
            public void onNestedExpression(String name, FieldExpressionParser subParser)
                throws ExpressionParsingException {
                subParser.setParserListener(this);
                subParser.parse();
            }
        };

        listenerTestParser = new FieldExpressionParser(PROJECT_MAPPING_EXRESSION);
        listener = new ObjectMapperBuilder(Project.class);

        project = new Project() {{
                setId(3L);
                setName("foobar");
                setConcrete(true);
                setOrganization(new Organization() {{
                    setName("orgName");
                    setId(2L);
                    setOwner(new User() {{
                        setId(1L);
                        setFirstName("orgOwnerFirstName");
                        setLastName("orgOwnerLastName");
                    }});
                }});
                setManager(new User() {{
                    setId(5L);
                    setFirstName("managerFirstName");
                    setLastName("managerLastName");
                }});
            }};
        mapper = projectMapperFactory.createMapper(Project.class, PROJECT_MAPPING_EXRESSION);
    }

    @Test
    @BenchmarkOptions(warmupRounds = 16, benchmarkRounds = 100)
    public void testParserPerformance() throws Exception {
        parserTestParser.setParserListener(listenerMock);
        parserTestParser.parse();
    }

    @Test
    @BenchmarkOptions(warmupRounds = 16, benchmarkRounds = 100)
    public void testBuilderPerformance() throws Exception {
        listenerTestParser.setParserListener(listener);
        listenerTestParser.parse();
    }

    @Test
    @BenchmarkOptions(warmupRounds = 16, benchmarkRounds = 100)
    public void testCachedBuilderPerformance() throws Exception {
        projectMapperFactory.createMapper(Project.class, PROJECT_MAPPING_EXRESSION);
    }

    @Test
    @BenchmarkOptions(warmupRounds = 16, benchmarkRounds = 100)
    public void testMappingPerformance() throws Exception {
        mapper.map(project);
    }
}
