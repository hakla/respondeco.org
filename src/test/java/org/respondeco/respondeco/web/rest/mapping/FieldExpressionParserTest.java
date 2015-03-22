package org.respondeco.respondeco.web.rest.mapping;

import org.junit.Before;
import org.junit.Test;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

/**
 * Created by clemens on 22/03/15.
 */
public class FieldExpressionParserTest {

    private final Logger log = LoggerFactory.getLogger(FieldExpressionParserTest.class);

    private FieldExpressionParser parser;
    private Project project;

    @Before
    public void setUp() {
        parser = new FieldExpressionParser();
        project = new Project();
        project.setId(3L);
        project.setName("foobar");
        project.setConcrete(true);
        project.setOrganization(new Organization() {{
            setName("asdf");
            setId(2L);
            setOwner(new User() {{
                id = 1l;
                setFirstName("asf");
                setLastName("bla");
            }});
        }});
    }

    @Test
    public void testSingleExpression() throws Exception {
        ObjectMapper mapper = parser.parse("id", Project.class);
        log.debug("mapper: {}", mapper);
    }

    @Test
    public void testSimpleExpression() throws Exception {
        ObjectMapper mapper = parser.parse("-purpose,concrete,organization(-id,name,owner)", Project.class);
        log.debug("mapper: {}", mapper);
        log.debug("{}", mapper.map(project));
    }

    @Test(expected = ExpressionDepthException.class)
    public void testDepthLimit_shouldThrowException() throws Exception {
        FieldExpressionParser parser1 = new FieldExpressionParser(1);
        ObjectMapper mapper = parser1.parse("id,concrete,organization(owner(firstName))", Project.class);
    }

    @Test(expected = NoSuchFieldException.class)
    public void testForbiddenFieldAccess_shouldThrowException() throws Exception {
        ObjectMapper mapper = parser.parse("id,name,projectLogo", Project.class);
        log.debug("mapper: {}", mapper);
    }

//    @Test
//    public void test1LevelNestedExpression() throws Exception {
//        FieldSet set = parser.parse("id,name,foo(xxx,-yyy),-bar,-baz");
//        log.debug("field set: {}", set);
//
//        assertTrue(set.contains(new SimpleFieldToken("id")));
//        assertTrue(set.contains(new NegatedFieldToken("baz")));
//        assertTrue(set.contains(new NestedFieldToken("foo", "xxx,-yyy")));
//    }
//
//    @Test
//    public void test2LevelNestedExpression() throws Exception {
//        FieldSet set = parser.parse("id,name,foo(xxx,-yyy,zzz(ccc,ddd,-eee)),-bar,-baz");
//        log.debug("field set: {}", set);
//
//        assertTrue(set.contains(new SimpleFieldToken("id")));
//        assertTrue(set.contains(new NegatedFieldToken("baz")));
//        assertTrue(set.contains(new NestedFieldToken("foo", "xxx,-yyy")));
//        assertTrue(set.getToken("foo").getChildren().contains(new NestedFieldToken("zzz", "ccc,ddd,-eee")));
//    }
//
//    @Test
//    public void testComplexNestedExpression() throws Exception {
//        FieldSet set = parser.parse("id,name,foo(xxx,-yyy,zzz(ccc,ddd,-eee)),-bar,-baz,ggg(cde),hhh(uuu,-vvv,www(-iii))");
//        log.debug("field set: {}", set);
//
//        assertTrue(set.contains(new SimpleFieldToken("id")));
//        assertTrue(set.contains(new NegatedFieldToken("baz")));
//        assertTrue(set.contains(new NestedFieldToken("foo", "xxx,-yyy")));
//        assertTrue(set.getToken("foo").getChildren().contains(new NestedFieldToken("zzz", "ccc,ddd,-eee")));
//        assertTrue(set.getToken("ggg").getChildren().contains(new SimpleFieldToken("cde")));
//        assertTrue(set.getToken("hhh").getChildren().contains(new NestedFieldToken("www", "-iii")));
//    }

}
