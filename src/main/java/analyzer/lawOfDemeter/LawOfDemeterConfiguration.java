package lawOfDemeter;

import config.Configurable;
import config.IConfiguration;

public class LawOfDemeterConfig implements Configurable {
    public static final String CONFIG_PATH = "lawOfDemeter.";
    public static final String FILL_COLOR = CONFIG_PATH + "fillColor";
    public static final String DEPENDS_LABEL = CONFIG_PATH + "label";

    private IConfiguration config;

    @Override
    public void setup(IConfiguration config) {
        this.config = config;
        this.config.setIfMissing(FILL_COLOR, "purple");
        this.config.setIfMissing(DEPENDS_LABEL, "Violates Law of Demeter");
    }

    public String getFillColor() {
        return this.config.getValue(FILL_COLOR);
    }

    public String getDependsLabel() {
        return this.config.getValue(DEPENDS_LABEL);
    }

}
