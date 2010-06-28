/**
 *  Copyright (C) 2010 dennis zhuang (killme2008@gmail.com)
 *
 *  This library is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation; either version 2.1 of the License, or
 *  (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 **/
package com.googlecode.aviator.test.function;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.exception.CompileExpressionErrorException;
import com.googlecode.aviator.exception.ExpressionRuntimeException;


/**
 * Aviator grammar test
 * 
 * @author dennis
 * 
 */
public class GrammarUnitTest {

    /**
     * 类型测试
     */
    @Test
    public void testType() {
        assertTrue(AviatorEvaluator.execute("1") instanceof Long);
        assertTrue(AviatorEvaluator.execute("3.2") instanceof Double);
        assertTrue(AviatorEvaluator.execute(Long.MAX_VALUE + "") instanceof Long);
        assertTrue(AviatorEvaluator.execute("3.14159265") instanceof Double);

        assertEquals("hello world", AviatorEvaluator.execute("'hello world'"));
        assertEquals("hello world", AviatorEvaluator.execute("\"hello world\""));
        assertEquals("hello \" world", AviatorEvaluator.execute("'hello \" world'"));
        assertEquals("hello 'world'", AviatorEvaluator.execute("\"hello 'world'\""));
        assertEquals("hello 'world' 'dennis'", AviatorEvaluator.execute("\"hello 'world' 'dennis'\""));

        assertTrue((Boolean) AviatorEvaluator.execute("true"));
        assertFalse((Boolean) AviatorEvaluator.execute("false"));

        assertEquals("/\\w+\\d?\\..*/", AviatorEvaluator.execute("/\\w+\\d?\\..*/"));
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("_a", 3);
        assertEquals(3, AviatorEvaluator.execute("_a", env));
        long now = System.currentTimeMillis();
        env.put("currentTime", now);
        assertEquals(now, AviatorEvaluator.execute("currentTime", env));

    }

    public class Foo {
        int a;


        public Foo() {

        }


        public Foo(int a) {
            super();
            this.a = a;
        }


        public int getA() {
            return a;
        }


        public void setA(int a) {
            this.a = a;
        }

    }

    public class Bar extends Foo {
        int b;


        public Bar() {

        }


        public Bar(int a, int b) {
            super(a);
            this.b = b;
        }


        public int getB() {
            return b;
        }


        public void setB(int b) {
            this.b = b;
        }

    }


    /**
     * 类型转换
     */
    @Test
    public void testTypeConversation() {
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("foo", new Foo(100));
        env.put("bar", new Bar(99, 999));
        env.put("date", new Date());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", "aviator");
        env.put("map", map);
        env.put("bool", Boolean.FALSE);

        // long op long=long
        assertTrue(AviatorEvaluator.execute("3+3") instanceof Long);
        assertTrue(AviatorEvaluator.execute("3+3/2") instanceof Long);
        assertTrue(AviatorEvaluator.execute("foo.a+bar.a", env) instanceof Long);
        assertEquals(1098L, AviatorEvaluator.execute("bar.a+bar.b", env));

        // double op double=double
        assertTrue(AviatorEvaluator.execute("3.2+3.3") instanceof Double);
        assertTrue(AviatorEvaluator.execute("3.01+3.1/2.1") instanceof Double);
        assertTrue(AviatorEvaluator.execute("3.19+3.1/2.9-1.0/(6.0002*7.7+8.9)") instanceof Double);

        // double + long=double
        assertTrue(AviatorEvaluator.execute("3+0.02") instanceof Double);
        assertTrue(AviatorEvaluator.execute("3+0.02-100") instanceof Double);
        assertTrue(AviatorEvaluator.execute("3+3/2-1/(6*7+8.0)") instanceof Double);
        assertTrue(AviatorEvaluator.execute("foo.a+3.2-1000", env) instanceof Double);

        // object + string =string
        assertEquals("hello world", AviatorEvaluator.execute("'hello '+ 'world'"));
        assertEquals("hello aviator", AviatorEvaluator.execute("'hello '+map.key", env));
        assertEquals("true aviator", AviatorEvaluator.execute("true+' '+map.key", env));
        assertEquals("100aviator", AviatorEvaluator.execute("foo.a+map.key", env));
        assertEquals("\\d+hello", AviatorEvaluator.execute("/\\d+/+'hello'"));
        assertEquals("3.2aviator", AviatorEvaluator.execute("3.2+map.key", env));
        assertEquals("false is false", AviatorEvaluator.execute("bool+' is false'", env));

    }


    @Test
    public void testNotOperandLimit() {
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("bool", false);

        assertFalse((Boolean) AviatorEvaluator.execute("!true"));
        assertTrue((Boolean) AviatorEvaluator.execute("!bool", env));

        try {
            AviatorEvaluator.execute("!3");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("!3.3");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("!/\\d+/");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("!'hello'");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }

    }


    @Test
    public void testNegOperandLimit() {
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("d", -3.3);

        assertEquals(-3L, AviatorEvaluator.execute("-3"));
        assertEquals(3.3, (Double) AviatorEvaluator.execute("-d", env), 0.001);

        try {
            AviatorEvaluator.execute("-true");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("-'hello'");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("-/\\d+/");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
    }


    @Test
    public void testAddOperandsLimit() {
        Map<String, Object> env = createEnv();

        assertEquals(6, AviatorEvaluator.execute("1+2+3"));
        assertEquals(2.7, (Double) AviatorEvaluator.execute("6+d", env), 0.001);
        assertEquals("hello aviator", AviatorEvaluator.execute("'hello '+s", env));
        assertEquals("-3.3aviator", AviatorEvaluator.execute("d+s", env));
        assertEquals("trueaviator", AviatorEvaluator.execute("bool+s", env));
        assertEquals("1aviator3", AviatorEvaluator.execute("1+s+3", env));

        Foo foo = new Foo(2);
        env.put("foo", foo);
        assertEquals(6, AviatorEvaluator.execute("1+foo.a+3", env));
        try {
            AviatorEvaluator.execute("foo+s", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }
        try {
            AviatorEvaluator.execute("d+bool", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }
        try {
            AviatorEvaluator.execute("1+bool+3", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }
        try {
            AviatorEvaluator.execute("/\\d+/+100", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }

    }


    @Test
    public void testSubOperandsLimit() {
        Map<String, Object> env = createEnv();
        assertEquals(3, AviatorEvaluator.execute("6-1-2", env));
        assertEquals(2.86, (Double) AviatorEvaluator.execute("6-3.14"), 0.001);
        assertEquals(4.3, (Double) AviatorEvaluator.execute("1-d", env), 0.001);
        assertEquals(0.0, (Double) AviatorEvaluator.execute("d-d", env), 0.001);
        assertEquals(1003.3, (Double) AviatorEvaluator.execute("a-d", env), 0.001);
        doArithOpIllegalOperands("-");
    }


    @Test
    public void testMultOperandsLimit() {
        Map<String, Object> env = createEnv();
        assertEquals(300, AviatorEvaluator.execute("100*3", env));
        assertEquals(18.84, (Double) AviatorEvaluator.execute("6*3.14"), 0.001);
        assertEquals(-9.9, (Double) AviatorEvaluator.execute("d*3", env), 0.001);
        assertEquals(10.89, (Double) AviatorEvaluator.execute("d*d", env), 0.001);
        assertEquals(-3300, (Double) AviatorEvaluator.execute("a*d", env), 0.001);
        doArithOpIllegalOperands("*");
    }


    @Test
    public void testDivOperandsLimit() {
        Map<String, Object> env = createEnv();
        assertEquals(33, AviatorEvaluator.execute("100/3", env));
        assertEquals(1.9108, (Double) AviatorEvaluator.execute("6/3.14"), 0.001);
        assertEquals(-1.1, (Double) AviatorEvaluator.execute("d/3", env), 0.001);
        assertEquals(1.0, (Double) AviatorEvaluator.execute("d/d", env), 0.001);
        assertEquals(-303.030, (Double) AviatorEvaluator.execute("a/d", env), 0.001);
        doArithOpIllegalOperands("/");
    }


    @Test
    public void testModOperandsLimit() {
        Map<String, Object> env = createEnv();
        assertEquals(1, AviatorEvaluator.execute("100%3", env));
        assertEquals(2.86, (Double) AviatorEvaluator.execute("6%3.14"), 0.001);
        assertEquals(-0.29999, (Double) AviatorEvaluator.execute("d%3", env), 0.001);
        assertEquals(0.0, (Double) AviatorEvaluator.execute("d%d", env), 0.001);
        assertEquals(1000 % -3.3, (Double) AviatorEvaluator.execute("a%d", env), 0.001);
        doArithOpIllegalOperands("%");
    }


    private void doArithOpIllegalOperands(String op) {
        try {
            AviatorEvaluator.execute("1" + op + "/\\d+/");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("true" + op + "true");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("'hello world'" + op + "'hello'");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("1" + op + "s");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }

        try {
            AviatorEvaluator.execute("bool" + op + "d");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("a" + op + "s");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("s" + op + "1000");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("bool" + op + "90.0");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("/hello/" + op + "/good/");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
    }


    @Test
    public void testMatch() {
        assertTrue((Boolean) AviatorEvaluator.execute("'10'=~/^\\d+$/"));
        assertTrue((Boolean) AviatorEvaluator.execute("'99'=~/^\\d+$/"));
        assertTrue((Boolean) AviatorEvaluator.execute("'0'=~/^\\d+$/"));
        assertFalse((Boolean) AviatorEvaluator.execute("'-3'=~/^\\d+$/"));
        assertFalse((Boolean) AviatorEvaluator.execute("'-0'=~/^\\d+$/"));
        assertFalse((Boolean) AviatorEvaluator.execute("'aviator'=~/^\\d+$/"));

        assertTrue((Boolean) AviatorEvaluator.execute("'10'=~/^[0-9]*[1-9][0-9]*$/"));
        assertTrue((Boolean) AviatorEvaluator.execute("'1'=~/^[0-9]*[1-9][0-9]*$/"));
        assertFalse((Boolean) AviatorEvaluator.execute("'0'=~/^[0-9]*[1-9][0-9]*$/"));
        assertFalse((Boolean) AviatorEvaluator.execute("'-3'=~/^[0-9]*[1-9][0-9]*$/"));
        assertFalse((Boolean) AviatorEvaluator.execute("'aviator'=~/^[0-9]*[1-9][0-9]*$/"));

        assertTrue((Boolean) AviatorEvaluator.execute("'-10'=~/^((-\\d+)|(0+))$/"));
        assertTrue((Boolean) AviatorEvaluator.execute("'-99'=~/^((-\\d+)|(0+))$/"));
        assertFalse((Boolean) AviatorEvaluator.execute("'99'=~/^((-\\d+)|(0+))$/"));
        assertFalse((Boolean) AviatorEvaluator.execute("'1'=~/^((-\\d+)|(0+))$/"));
        assertFalse((Boolean) AviatorEvaluator.execute("'aviator'=~/^((-\\d+)|(0+))$/"));

        // ^-?\d+$
        assertTrue((Boolean) AviatorEvaluator.execute("'-10'=~/^-?\\d+$/"));
        assertTrue((Boolean) AviatorEvaluator.execute("'0'=~/^-?\\d+$/"));
        assertTrue((Boolean) AviatorEvaluator.execute("'10'=~/^-?\\d+$/"));
        assertFalse((Boolean) AviatorEvaluator.execute("'aviator'=~/^-?\\d+$/"));

        assertTrue((Boolean) AviatorEvaluator.execute("'abc'=~/^[A-Za-z]+$/"));
        assertTrue((Boolean) AviatorEvaluator.execute("'ABC'=~/^[A-Za-z]+$/"));
        assertFalse((Boolean) AviatorEvaluator.execute("'123'=~/^[A-Za-z]+$/"));

        assertFalse((Boolean) AviatorEvaluator.execute("'abc'=~/^[A-Z]+$/"));
        assertTrue((Boolean) AviatorEvaluator.execute("'ABC'=~/^[A-Z]+$/"));

        assertTrue((Boolean) AviatorEvaluator.execute("'abc'=~/^[a-z]+$/"));
        assertFalse((Boolean) AviatorEvaluator.execute("'ABC'=~/^[a-z]+$/"));

        assertTrue((Boolean) AviatorEvaluator
            .execute("'0595-97357355'=~/^((\\+?[0-9]{2,4}\\-[0-9]{3,4}\\-)|([0-9]{3,4}\\-))?([0-9]{7,8})(\\-[0-9]+)?$/"));
        assertTrue((Boolean) AviatorEvaluator
            .execute("'0595-3749306-020'=~/^((\\+?[0-9]{2,4}\\-[0-9]{3,4}\\-)|([0-9]{3,4}\\-))?([0-9]{7,8})(\\-[0-9]+)?$/"));
        assertFalse((Boolean) AviatorEvaluator
            .execute("'0595-abc'=~/^((\\+?[0-9]{2,4}\\-[0-9]{3,4}\\-)|([0-9]{3,4}\\-))?([0-9]{7,8})(\\-[0-9]+)?$/"));

        assertTrue((Boolean) AviatorEvaluator.execute("'455729032'=~/^[1-9]*[1-9][0-9]*$/"));
        assertFalse((Boolean) AviatorEvaluator.execute("'45d729032'=~/^[1-9]*[1-9][0-9]*$/"));
        assertTrue((Boolean) AviatorEvaluator.execute("'<html>hello</html>'=~/<(.*)>.*<\\/\\1>|<(.*) \\/>/"));
        assertTrue((Boolean) AviatorEvaluator
            .execute("'127.0.0.1'=~/^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5]).(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5]).(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5]).(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$/"));

        assertFalse((Boolean) AviatorEvaluator
            .execute("'127.0.0.'=~/^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5]).(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5]).(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5]).(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$/"));
    }


    @Test
    public void testComparePattern() {
        Map<String, Object> env = createEnv();

        assertTrue((Boolean) AviatorEvaluator.execute("p1==p1", env));
        assertTrue((Boolean) AviatorEvaluator.execute("p1>=p1", env));
        assertTrue((Boolean) AviatorEvaluator.execute("p1<=p1", env));
        assertTrue((Boolean) AviatorEvaluator.execute("p1<p2", env));
        assertTrue((Boolean) AviatorEvaluator.execute("p2>p1", env));
        assertFalse((Boolean) AviatorEvaluator.execute("p1>=p2", env));
        assertFalse((Boolean) AviatorEvaluator.execute("p2<=p1", env));
        assertTrue((Boolean) AviatorEvaluator.execute("/aviator/>/abc/", env));
        assertFalse((Boolean) AviatorEvaluator.execute("/aviator/</abc/", env));
        try {
            AviatorEvaluator.execute("3>/abc/");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("'abc'!=/abc/");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("3.999==p1", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("false==p1", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("p2<=bool", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
    }


    @Test
    public void testCompareString() {
        Map<String, Object> env = createEnv();
        assertTrue((Boolean) AviatorEvaluator.execute("'b'>'a'"));
        assertTrue((Boolean) AviatorEvaluator.execute("'b'>='a'"));
        assertTrue((Boolean) AviatorEvaluator.execute("'b'!='a'"));
        assertFalse((Boolean) AviatorEvaluator.execute("'b'<'a'"));
        assertFalse((Boolean) AviatorEvaluator.execute("'b'<='a'"));

        assertTrue((Boolean) AviatorEvaluator.execute("s==s", env));
        assertTrue((Boolean) AviatorEvaluator.execute("s>'abc'", env));
        assertFalse((Boolean) AviatorEvaluator.execute("s<'abc'", env));
        assertFalse((Boolean) AviatorEvaluator.execute("s<='abc'", env));
        assertTrue((Boolean) AviatorEvaluator.execute("s!='abc'", env));
        assertTrue((Boolean) AviatorEvaluator.execute("s>'abc'", env));
        assertTrue((Boolean) AviatorEvaluator.execute("s==s", env));

        try {
            AviatorEvaluator.execute("bool>s", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("true<'abc'");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("s>bool", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("100=='hello'");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("s!=d", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("/\\d+/<=s", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("'hello'==/[a-zA-Z]/");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
    }


    @Test
    public void testCompareNumber() {
        Map<String, Object> env = createEnv();
        assertTrue((Boolean) AviatorEvaluator.execute("3>1"));
        assertTrue((Boolean) AviatorEvaluator.execute("3>=1"));
        assertTrue((Boolean) AviatorEvaluator.execute("3!=1"));
        assertFalse((Boolean) AviatorEvaluator.execute("3<1"));
        assertFalse((Boolean) AviatorEvaluator.execute("3<=1"));
        assertFalse((Boolean) AviatorEvaluator.execute("3==1"));

        assertTrue((Boolean) AviatorEvaluator.execute("3>=3"));
        assertTrue((Boolean) AviatorEvaluator.execute("3<=3"));
        assertTrue((Boolean) AviatorEvaluator.execute("d<0", env));
        assertTrue((Boolean) AviatorEvaluator.execute("a>3", env));
        assertTrue((Boolean) AviatorEvaluator.execute("d>=d", env));
        assertFalse((Boolean) AviatorEvaluator.execute("d>0", env));
        assertFalse((Boolean) AviatorEvaluator.execute("d>=0", env));
        assertFalse((Boolean) AviatorEvaluator.execute("a<3", env));
        assertFalse((Boolean) AviatorEvaluator.execute("d>=3", env));
        assertFalse((Boolean) AviatorEvaluator.execute("a<=3", env));

        assertTrue((Boolean) AviatorEvaluator.execute("a>=a", env));
        assertTrue((Boolean) AviatorEvaluator.execute("a>d", env));
        assertTrue((Boolean) AviatorEvaluator.execute("d<a", env));

        try {
            AviatorEvaluator.execute("bool>3", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("true<100");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("d>bool", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("100=='hello'");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("'good'>a", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("/\\d+/>3");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("4.9==/[a-zA-Z]/");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
    }


    @Test
    public void testLogicOr() {
        assertTrue((Boolean) AviatorEvaluator.execute("true||true"));
        assertTrue((Boolean) AviatorEvaluator.execute("true||false"));
        assertTrue((Boolean) AviatorEvaluator.execute("false||true"));
        assertFalse((Boolean) AviatorEvaluator.execute("false||false"));
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("a", Boolean.FALSE);
        env.put("b", Boolean.TRUE);
        env.put("s", "hello");
        env.put("c", 3.3);

        assertTrue((Boolean) AviatorEvaluator.execute("b||b", env));
        assertTrue((Boolean) AviatorEvaluator.execute("b||a", env));
        assertTrue((Boolean) AviatorEvaluator.execute("a||b", env));
        assertFalse((Boolean) AviatorEvaluator.execute("a||a", env));

        try {
            AviatorEvaluator.execute("3 || true");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }
        try {
            AviatorEvaluator.execute("false || 3");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }
        try {
            AviatorEvaluator.execute("c || 3", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }
        try {
            AviatorEvaluator.execute("false || c", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }

        try {
            AviatorEvaluator.execute("false || s", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }
        try {
            AviatorEvaluator.execute("c || s", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("/test/ || s", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        // 测试短路
        assertTrue((Boolean) AviatorEvaluator.execute("true || s"));
        assertTrue((Boolean) AviatorEvaluator.execute("true || c"));
        assertTrue((Boolean) AviatorEvaluator.execute("true || 3"));
        assertTrue((Boolean) AviatorEvaluator.execute("true || /hello/"));
    }


    @Test
    public void testLogicAnd() {
        assertTrue((Boolean) AviatorEvaluator.execute("true&&true"));
        assertFalse((Boolean) AviatorEvaluator.execute("true&&false"));
        assertFalse((Boolean) AviatorEvaluator.execute("false && true"));
        assertFalse((Boolean) AviatorEvaluator.execute("false    &&false"));
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("a", Boolean.FALSE);
        env.put("b", Boolean.TRUE);
        env.put("s", "hello");
        env.put("c", 3.3);

        assertTrue((Boolean) AviatorEvaluator.execute("b&&  b", env));
        assertFalse((Boolean) AviatorEvaluator.execute("b    &&a", env));
        assertFalse((Boolean) AviatorEvaluator.execute("a&&b", env));
        assertFalse((Boolean) AviatorEvaluator.execute("a    &&    a", env));

        try {
            AviatorEvaluator.execute("3 && true");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }
        try {
            AviatorEvaluator.execute("true && 3");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }
        try {
            AviatorEvaluator.execute("c && 3", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }
        try {
            AviatorEvaluator.execute("true && c", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }

        try {
            AviatorEvaluator.execute("true && s", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }
        try {
            AviatorEvaluator.execute("c&& s", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        try {
            AviatorEvaluator.execute("/test/ && s", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {
        }
        // 测试短路
        assertFalse((Boolean) AviatorEvaluator.execute("false && s"));
        assertFalse((Boolean) AviatorEvaluator.execute("false &&  c"));
        assertFalse((Boolean) AviatorEvaluator.execute("false &&  3"));
        assertFalse((Boolean) AviatorEvaluator.execute("false &&  /hello/"));

    }


    /*
     * 测试三元表达式
     */
    @Test
    public void testTernaryOperator() {
        Map<String, Object> env = new HashMap<String, Object>();
        int i = 0;
        float f = 3.14f;
        String email = "killme2008@gmail.com";
        char ch = 'a';
        boolean t = true;
        env.put("i", i);
        env.put("f", f);
        env.put("email", email);
        env.put("ch", ch);
        env.put("t", t);

        assertEquals(1, AviatorEvaluator.execute("2>1?1:0"));
        assertEquals(0, AviatorEvaluator.execute("2<1?1:0"));
        assertEquals(f, (Float) AviatorEvaluator.execute("false?i:f", env), 0.001);
        assertEquals(i, AviatorEvaluator.execute("true?i:f", env));
        assertEquals("killme2008", AviatorEvaluator.execute("email=~/([\\w0-9]+)@\\w+\\.\\w+/ ? $1:'unknow'", env));

        assertEquals(f, (Float) AviatorEvaluator.execute("ch!='a'?i:f", env), 0.001);
        assertEquals(i, AviatorEvaluator.execute("ch=='a'?i:f", env));
        assertEquals(email, AviatorEvaluator.execute("t?email:ch", env));

        // 多层嵌套
        assertEquals(ch, AviatorEvaluator.execute("t? i>0? f:ch : email", env));

        assertEquals(email, AviatorEvaluator.execute("!t? i>0? f:ch : f>3?email:ch", env));

        // 使用括号
        assertEquals(email, AviatorEvaluator.execute("!t? (i>0? f:ch) :( f>3?email:ch)", env));
        // 测试错误情况
        try {
            AviatorEvaluator.execute("f?1:0", env);
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }
        try {
            AviatorEvaluator.execute("'hello'?1:0");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }
        try {
            AviatorEvaluator.execute("/test/?1:0");
            Assert.fail();
        }
        catch (ExpressionRuntimeException e) {

        }
        try {
            AviatorEvaluator.execute("!t? (i>0? f:ch) : f>3?email:ch)", env);
            Assert.fail();
        }
        catch (CompileExpressionErrorException e) {

        }
        try {
            AviatorEvaluator.execute("!t? (i>0? f:ch : (f>3?email:ch)", env);
            Assert.fail();
        }
        catch (CompileExpressionErrorException e) {

        }
    }


    /**
     * 测试nil
     */
    @Test
    public void testNilObject() {
        assertTrue((Boolean) AviatorEvaluator.execute("a==nil"));
        assertTrue((Boolean) AviatorEvaluator.execute("nil==a"));

        assertFalse((Boolean) AviatorEvaluator.execute("3==nil"));
        assertTrue((Boolean) AviatorEvaluator.execute("3!=nil"));

        assertFalse((Boolean) AviatorEvaluator.execute("3.5==nil"));
        assertTrue((Boolean) AviatorEvaluator.execute("3.5!=nil"));

        assertFalse((Boolean) AviatorEvaluator.execute("true==nil"));
        assertTrue((Boolean) AviatorEvaluator.execute("true!=nil"));

        assertFalse((Boolean) AviatorEvaluator.execute("false==nil"));
        assertTrue((Boolean) AviatorEvaluator.execute("false!=nil"));

        assertTrue((Boolean) AviatorEvaluator.execute("nil==nil"));
        assertFalse((Boolean) AviatorEvaluator.execute("nil!=nil"));

        assertFalse((Boolean) AviatorEvaluator.execute("'a'==nil"));
        assertTrue((Boolean) AviatorEvaluator.execute("'a'!=nil"));

        assertFalse((Boolean) AviatorEvaluator.execute("/\\d+/==nil"));
        assertTrue((Boolean) AviatorEvaluator.execute("/\\d+/!=nil"));

        Map<String, Object> env = createEnv();
        assertFalse((Boolean) AviatorEvaluator.execute("p1==nil", env));
        assertTrue((Boolean) AviatorEvaluator.execute("p1>nil", env));

        assertFalse((Boolean) AviatorEvaluator.execute("d==nil", env));
        assertTrue((Boolean) AviatorEvaluator.execute("d>nil", env));

        assertFalse((Boolean) AviatorEvaluator.execute("s==nil", env));
        assertTrue((Boolean) AviatorEvaluator.execute("s>nil", env));

        assertFalse((Boolean) AviatorEvaluator.execute("bool==nil", env));
        assertTrue((Boolean) AviatorEvaluator.execute("bool>nil", env));

        assertFalse((Boolean) AviatorEvaluator.execute("a==nil", env));
        assertTrue((Boolean) AviatorEvaluator.execute("a>nil", env));

        // null == null
        assertTrue((Boolean) AviatorEvaluator.execute("a==b"));
        assertFalse((Boolean) AviatorEvaluator.execute("'s'==a"));
        assertTrue((Boolean) AviatorEvaluator.execute("'s'>=a"));
        assertTrue((Boolean) AviatorEvaluator.execute("'s'>a"));
        assertTrue((Boolean) AviatorEvaluator.execute("bool>unknow",env));

    }
    
    @Test
    public void testFunctionCall(){
        
    }


    private Map<String, Object> createEnv() {
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("d", -3.3);
        env.put("s", "aviator");
        env.put("bool", true);
        env.put("a", 1000);
        env.put("p1", "[a-z-A-Z]+");
        env.put("p2", "\\d+\\.\\d+");
        return env;
    }
}
