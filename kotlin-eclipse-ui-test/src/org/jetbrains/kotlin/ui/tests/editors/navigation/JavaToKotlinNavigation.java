package org.jetbrains.kotlin.ui.tests.editors.navigation;

import org.junit.Test;

public class JavaToKotlinNavigation extends JavaToKotlinNavigationTestCase {
	@Override
	protected String getTestDataRelativePath() {
		return "navigation/javaToKotlin";
	}
	
	@Test
	public void toKotlinClass() {
		doAutoTest();
	}
	
	@Test
	public void toKotlinClassInPackage() {
		doAutoTest();
	}
	
	@Test
	public void toInnerKotlinClass() {
		doAutoTest();
	}
	
	@Test
	public void toKotlinTopLevelFunction() {
		doAutoTest();
	}
	
	@Test
	public void toKotlinTopLevelFunctionWithParams() {
		doAutoTest();
	}
	
	@Test
	public void toKotlinFunction() {
		doAutoTest();
	}
	
	@Test
	public void toKotlinFunctionInCompanion() {
		doAutoTest();
	}
	
	@Test
	public void toCompanionObject() {
		doAutoTest();
	}
	
	@Test
	public void toNamedCompanionObject() {
		doAutoTest();
	}
	
	@Test
	public void toFunctionInInnerClass() {
		doAutoTest();
	}
	
	@Test
	public void toFunctionWithNameDuplicate() {
		doAutoTest();
	}
	
	@Test
	public void toFunctionWithNameDuplicateInClass() {
		doAutoTest();
	}
}
