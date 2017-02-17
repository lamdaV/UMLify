package staticInitializer;

import config.Configurable;
import config.IConfiguration;

public class StaticInitializerConfiguration implements Configurable{

	public static final String CONFIG_PATH = "staticinitializer.";
    public static final String COLOR = CONFIG_PATH + "color";
	
    private IConfiguration config;

    @Override
    public void setup(IConfiguration config) {
        this.config = config;
        this.config.setIfMissing(StaticInitializerConfiguration.COLOR, "green");
    }

    public String getFavorComColor() {
        return this.config.getValue(StaticInitializerConfiguration.COLOR);
    }

}
