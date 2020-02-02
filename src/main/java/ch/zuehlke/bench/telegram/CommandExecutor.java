package ch.zuehlke.bench.telegram;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;

import ch.zuehlke.bench.skiarea.TitlisService;
import ch.zuehlke.bench.transport.DelayService;
import ch.zuehlke.bench.weather.WeatherService;

@ApplicationScoped
public class CommandExecutor {

    public static final String PREFIX = "/";


    public String execute(COMMAND currentCommand) {
        TelegramCommand cmd = (TelegramCommand) CDI.current().select(currentCommand.clazz, new AnnotationLiteral<Any>() {
        }).get();
        return cmd.execute(currentCommand.parameter);
    }

    enum COMMAND {
        LUZ_BRN(DelayService.class, "LUZ", "BRN"),
        BRN_LUZ(DelayService.class, "BRN", "LUZ"),
        LUZ_BAS(DelayService.class, "LUZ", "BAS"),
        BAS_LUZ(DelayService.class, "BAS", "LUZ"),
        LUZ_ZRH(DelayService.class, "LUZ", "ZRH"),
        ZRH_LUZ(DelayService.class, "ZRH", "LUZ"),
        LUZ(WeatherService.class, "LUZ"),
        TIT(WeatherService.class, "TIT"),
        BER(WeatherService.class, "BER"),
        ENG(WeatherService.class, "ENG"),
        LIFT(TitlisService.class, null);


        private Class clazz;
        private String[] parameter;

        COMMAND(Class clazz, String... parameter) {
            this.clazz = clazz;
            this.parameter = parameter;
        }

        public String[] getParameter() {
            return parameter;
        }
    }

    public static Optional<COMMAND> parseCommand(String command) {
        if (command == null || command.isEmpty() || !command.startsWith(PREFIX)) {
            return null;
        }

        String plainCmd = command.substring(1);

        for (COMMAND cmd : COMMAND.values()) {
            if (plainCmd.endsWith(cmd.name())) {
                return Optional.of(cmd);
            }
        }
        return Optional.empty();
    }


}
