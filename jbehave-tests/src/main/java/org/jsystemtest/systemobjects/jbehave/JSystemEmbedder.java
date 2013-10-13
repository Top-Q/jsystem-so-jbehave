package org.jsystemtest.systemobjects.jbehave;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;

import org.apache.commons.io.FileUtils;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.parsers.RegexPrefixCapturingPatternParser;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.SilentStepMonitor;
import org.jsystemtest.systemobjects.utils.BeanUtils;

/**
 * Embedding JBheave execution engine with the JSystem framework
 * 
 * @author Itai.Agmon
 * 
 */
public class JSystemEmbedder extends Embedder {

	protected Reporter report = ListenerstManager.getInstance();

	private final List<Class<?>> stepsClasses;

	public JSystemEmbedder(List<Class<?>> stepsClasses) {
		super();
		this.stepsClasses = stepsClasses;
	}

	@Override
	public EmbedderControls embedderControls() {
		return new EmbedderControls().doIgnoreFailureInStories(true).doIgnoreFailureInView(true);
	}

	@Override
	public Configuration configuration() {
		Class<? extends JSystemEmbedder> embedderClass = this.getClass();
		return new MostUsefulConfiguration()
				.useStoryLoader(new LoadFromClasspath(embedderClass.getClassLoader()))
				.useStoryReporterBuilder(
						new StoryReporterBuilder()
								.withCodeLocation(CodeLocations.codeLocationFromClass(embedderClass))
								.withDefaultFormats()
								.withCodeLocation(CodeLocations.codeLocationFromPath(getReportsFolder()))
								.withFormats(org.jbehave.core.reporters.Format.CONSOLE, Format.HTML, Format.XML,
										Format.TXT).withCrossReference(new CrossReference()))
				.useStepPatternParser(new RegexPrefixCapturingPatternParser("%"))
				.useStepMonitor(new SilentStepMonitor());
	}

	/**
	 * Run the stories and
	 */
	@Override
	public void runStoriesAsPaths(List<String> storyPaths) {
		List<String> sotriesInClasspath = addToClassPath(storyPaths);
		super.runStoriesAsPaths(sotriesInClasspath);
		report.addLink("JBehave reports", "jbehave");

	}

	/**
	 * Adding all the stories to the class path
	 * 
	 * @param storyPaths
	 * @return a list of stories that are included in the class path
	 */
	private List<String> addToClassPath(List<String> storyPaths) {
		final List<String> storiesInClasspath = new ArrayList<String>();
		for (String story : storyPaths) {
			if (BeanUtils.isInClasspath(story)) {
				storiesInClasspath.add(story);
			} else {
				final File storyFile = new File(story);
				if (!storyFile.exists()) {
					report.report("Story " + story + " is not exist", Reporter.WARNING);
					continue;
				}
				File storiesFolder = new File(JSystemProperties.getInstance().getPreference(
						FrameworkOptions.TESTS_CLASS_FOLDER)
						+ File.separator + "stories");
				if (!storiesFolder.exists() || !storiesFolder.isDirectory()) {
					if (!storiesFolder.mkdir()) {
						report.report("Failed creating stories folder in class folder. Stories that are not in the classpath could not be executed");
						continue;
					}
				}
				try {
					FileUtils.copyFile(storyFile, new File(storiesFolder, storyFile.getName()));
				} catch (IOException e) {
					report.report("Failed copying story " + story + " to class folder", Reporter.WARNING);
				}
				storiesInClasspath.add("stories/" + storyFile.getName());

			}
		}
		return storiesInClasspath;
	}

	/**
	 * @return JSystem current reports folder with the JBehave folder suffix
	 */
	private String getReportsFolder() {
		return JSystemProperties.getInstance().getPreference(FrameworkOptions.LOG_FOLDER) + File.separator + "current"
				+ File.separator + "jbehave";
	}

	@Override
	public InjectableStepsFactory stepsFactory() {
		List<Object> steps = BeanUtils.getAsInstances(stepsClasses);
		if (steps.size() != stepsClasses.size()) {
			report.report("Not all test classes were initialized successfully. Some stories may not run properly.",
					Reporter.WARNING);
		}
		return new InstanceStepsFactory(configuration(), steps);
	}

}