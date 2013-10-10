package il.co.topq.auto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;

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
								.withCodeLocation(
										CodeLocations.codeLocationFromPath(getReportsFolder()))
								// .withCodeLocation(CodeLocations.codeLocationFromPath("C:/Temp/jbehave"))
								.withFormats(org.jbehave.core.reporters.Format.CONSOLE, Format.HTML, Format.XML,
										Format.TXT).withCrossReference(new CrossReference()))
				// .useParameterConverters(new ParameterConverters()
				// .addConverters(new DateConverter(new
				// SimpleDateFormat("yyyy-MM-dd")))) // use custom date pattern
				.useStepPatternParser(new RegexPrefixCapturingPatternParser("%"))

				.useStepMonitor(new SilentStepMonitor());
	}
	
	 public void runStoriesAsPaths(List<String> storyPaths) {
		 super.runStoriesAsPaths(storyPaths);
		 report.addLink("JBehave reports", "jbehave");
		 
		 
	 }
	 
		private String getReportsFolder(){
			return JSystemProperties.getInstance()
				.getPreference(
						FrameworkOptions.LOG_FOLDER)+ File.separator + "current"
						+ File.separator + "jbehave";
		 }

	@Override
	public InjectableStepsFactory stepsFactory() {
		List<Object> steps = getAsInstances(stepsClasses);
		return new InstanceStepsFactory(configuration(), steps);
	}

	private List<Object> getAsInstances(List<Class<?>> stepsClasses2) {
		List<Object> instances = new ArrayList<Object>();
		for (Class<?> clazz : stepsClasses2) {
			try {
				Object instance = clazz.newInstance();
				instances.add(instance);

			} catch (ReflectiveOperationException e) {
				// TODO: Handle exception
			}
		}
		return instances;
	}

}