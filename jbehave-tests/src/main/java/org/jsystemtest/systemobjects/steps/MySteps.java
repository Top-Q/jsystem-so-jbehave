package org.jsystemtest.systemobjects.steps;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Assert;

public class MySteps {
	protected Reporter report = ListenerstManager.getInstance();

	@Given("I am a pending step")
	public void givenIAmAPendingStep() {
		report.report("In pending step");
	}

	@Given("I am still pending step")
	public void givenIAmStillPendingStep() {
		report.report("I am still pending step");
	}

	@When("a good soul will implement me")
	public void whenAGoodSoulWillImplementMe() {
		report.report("a good soul will implement me");
	}

	@Then("I shall be happy")
	public void thenIShallBeHappy() throws Exception {
		report.report("I shall be happy");
		throw new Exception("This is an exception");
//		Assert.assertNotNull("This is a junit failure", null);
	}
}
