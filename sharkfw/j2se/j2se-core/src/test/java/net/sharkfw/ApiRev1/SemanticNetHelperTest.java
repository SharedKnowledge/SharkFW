/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.apirev1;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Enumeration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSemanticNet;


public class SemanticNetHelperTest {

	SemanticNet sn;

	@Before
	public void setUp() throws Exception {
		sn = new InMemoSemanticNet();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testEmpty() throws SharkKBException {
		assertTrue(SharkCSAlgebra.isTaxonomy(sn, "super"));
	}

	@Test
	public final void testOne() throws SharkKBException {
		sn.createSemanticTag("Java", "https://en.wikipedia.org/wiki/Java_(programming_language)");
		assertTrue(SharkCSAlgebra.isTaxonomy(sn, "super"));
	}

	@Test
	public final void testTwoWithoutConnection() throws SharkKBException {
		sn.createSemanticTag("Java", "1");
		sn.createSemanticTag("Programming Languages", "2");
		assertTrue(SharkCSAlgebra.isTaxonomy(sn, "super"));
	}

	@Test
	public final void testTwoWithOneConnection() throws SharkKBException {
		SNSemanticTag java = sn.createSemanticTag("Java", "1");
		SNSemanticTag pl = sn.createSemanticTag("Programming Languages", "2");

		sn.setPredicate(pl, java, "super");

		assertTrue(SharkCSAlgebra.isTaxonomy(sn, "super"));
	}

	@Test
	public final void testOneWithConnectionToItself() throws SharkKBException {
		SNSemanticTag god = sn.createSemanticTag("God", "1");
		sn.createSemanticTag("Jesus", "2");
		sn.createSemanticTag("Holy Spirit", "3");

		sn.setPredicate(god, god, "super");

		assertFalse(SharkCSAlgebra.isTaxonomy(sn, "super"));
	}

	@Test
	public final void testThreeWithCircularConnection() throws SharkKBException {
		SNSemanticTag god = sn.createSemanticTag("God", "1");
		SNSemanticTag jesus = sn.createSemanticTag("Jesus", "2");
		SNSemanticTag holySpirit = sn.createSemanticTag("Holy Spirit", "3");

		sn.setPredicate(god, jesus, "super");
		sn.setPredicate(jesus, holySpirit, "super");
		sn.setPredicate(holySpirit, god, "super");

		assertFalse(SharkCSAlgebra.isTaxonomy(sn, "super"));
	}

	@Test
	public final void testLargeNetWithManyCircularConnections() throws SharkKBException {
		// Example from here:
		// https://github.com/bwesterb/py-tarjan/blob/350f646c86556fd76d26c0a413ca34375371c60b/README.rst

		SNSemanticTag st1 = sn.createSemanticTag("1", "1");
		SNSemanticTag st2 = sn.createSemanticTag("2", "2");
		SNSemanticTag st3 = sn.createSemanticTag("3", "3");
		SNSemanticTag st4 = sn.createSemanticTag("4", "4");
		SNSemanticTag st5 = sn.createSemanticTag("5", "5");
		SNSemanticTag st6 = sn.createSemanticTag("6", "6");
		SNSemanticTag st7 = sn.createSemanticTag("7", "7");
		SNSemanticTag st8 = sn.createSemanticTag("8", "8");
		SNSemanticTag st9 = sn.createSemanticTag("9", "9");

		sn.setPredicate(st1, st2, "super");
		sn.setPredicate(st2, st1, "super");
		sn.setPredicate(st2, st5, "super");
		sn.setPredicate(st3, st4, "super");
		sn.setPredicate(st4, st3, "super");
		sn.setPredicate(st4, st5, "super");
		sn.setPredicate(st5, st6, "super");
		sn.setPredicate(st6, st7, "super");
		sn.setPredicate(st7, st8, "super");
		sn.setPredicate(st8, st6, "super");
		sn.setPredicate(st8, st9, "super");

		assertFalse(SharkCSAlgebra.isTaxonomy(sn, "super"));
	}

	@Test
	public final void testTransitiveFalse() throws SharkKBException {
		SNSemanticTag a = sn.createSemanticTag("a", "1");
		SNSemanticTag b = sn.createSemanticTag("b", "2");
		SNSemanticTag c = sn.createSemanticTag("c", "3");

		sn.setPredicate(a, b, "super");
		sn.setPredicate(b, c, "super");

		assertFalse(SharkCSAlgebra.isTransitive(sn, "super"));
	}

	@Test
	public final void testTransitiveTrue() throws SharkKBException {
		SNSemanticTag a = sn.createSemanticTag("a", "1");
		SNSemanticTag b = sn.createSemanticTag("b", "2");
		SNSemanticTag c = sn.createSemanticTag("c", "3");

		SNSemanticTag d = sn.createSemanticTag("d", "4");
		SNSemanticTag e = sn.createSemanticTag("e", "5");

		sn.setPredicate(a, b, "super");
		sn.setPredicate(b, c, "super");
		sn.setPredicate(a, c, "super");

		sn.setPredicate(e, d, "super");

		assertTrue(SharkCSAlgebra.isTransitive(sn, "super"));
	}
	
	@Test
	public final void testSymmetricTrue() throws SharkKBException {
		SNSemanticTag a = sn.createSemanticTag("a", "1");
		SNSemanticTag b = sn.createSemanticTag("b", "2");
		SNSemanticTag c = sn.createSemanticTag("c", "3");
		SNSemanticTag d = sn.createSemanticTag("d", "4");
		
		sn.setPredicate(a, b, "super");
		sn.setPredicate(b, a, "super");
		sn.setPredicate(b, d, "super");
		sn.setPredicate(d, b, "super");
		
		assertTrue(SharkCSAlgebra.isSymmetric(sn, "super"));
	}
	
	@Test
	public final void testSymmetricFalse() throws SharkKBException {
		SNSemanticTag a = sn.createSemanticTag("a", "1");
		SNSemanticTag b = sn.createSemanticTag("b", "2");
		SNSemanticTag c = sn.createSemanticTag("c", "3");
		SNSemanticTag d = sn.createSemanticTag("d", "4");
		
		sn.setPredicate(a, b, "super");
		sn.setPredicate(b, d, "super");
		sn.setPredicate(d, b, "super");
		
		assertFalse(SharkCSAlgebra.isSymmetric(sn, "super"));
	}
	
	@Test
	public final void testMakeTransitive() throws SharkKBException {
		SNSemanticTag a = sn.createSemanticTag("a", "1");
		SNSemanticTag b = sn.createSemanticTag("b", "2");
		SNSemanticTag c = sn.createSemanticTag("c", "3");

		sn.setPredicate(a, b, "super");
		sn.setPredicate(b, c, "super");
		
		SharkCSAlgebra.makeTransitive(sn, "super");
		
		assertTrue(SharkCSAlgebra.isTransitive(sn, "super"));
		   
		ArrayList<SNSemanticTag> subTagsList = new ArrayList<>();
		Enumeration<SNSemanticTag> subTags = a.targetTags("super");
		while (subTags.hasMoreElements()) {
			subTagsList.add(subTags.nextElement());
		}
		assertTrue(subTagsList.contains(c));
	}
	
	@Test
	public final void testMakeSymmetric() throws SharkKBException {
		SNSemanticTag a = sn.createSemanticTag("a", "1");
		SNSemanticTag b = sn.createSemanticTag("b", "2");
		SNSemanticTag c = sn.createSemanticTag("c", "3");
		SNSemanticTag d = sn.createSemanticTag("d", "4");
		
		sn.setPredicate(a, b, "super");
		sn.setPredicate(b, c, "super");
		sn.setPredicate(c, d, "super");
		
		SharkCSAlgebra.makeSymmetric(sn, "super");
		
		assertTrue(SharkCSAlgebra.isSymmetric(sn, "super"));
		
		ArrayList<SNSemanticTag> subTagsList = new ArrayList<>();
		Enumeration<SNSemanticTag> subTags = b.targetTags("super");
		while (subTags.hasMoreElements()) {
			subTagsList.add(subTags.nextElement());
		}
		assertTrue(subTagsList.contains(a));
	}

}