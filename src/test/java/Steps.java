import com.thoughtworks.gauge.Step;

public class Steps {
  @Step("Hello!")
  public void hello() {
    StepsDef.hello();
  }
}
