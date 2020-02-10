package ch.zuehlke.bench.telegram;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;

import ch.zuehlke.bench.skiarea.TitlisService;
import ch.zuehlke.bench.transport.DelayService;
import ch.zuehlke.bench.weather.FoehnService;
import ch.zuehlke.bench.weather.WeatherService;

@ApplicationScoped
public class CommandExecutor {

    public static final String PREFIX = "/";


    public String execute(COMMAND currentCommand) {
        TelegramCommand cmd = (TelegramCommand) CDI.current().select(currentCommand.clazz, new AnnotationLiteral<Any>() {
        }).get();
        return cmd.execute(currentCommand.parameter);
    }

    public static Optional<COMMAND> parseCommand(String command) {
        if (command == null || command.isEmpty() || !command.startsWith(PREFIX)) {
            return null;
        }

        String plainCmd = command.substring(1);

        for (COMMAND cmd : COMMAND.values()) {
            if (plainCmd.equalsIgnoreCase(cmd.name())) {
                return Optional.of(cmd);
            }
        }
        return Optional.empty();
    }

    enum COMMAND {
        LUZ_BRN(DelayService.class, false, "LUZ", "BRN", "0010000000"),
        BRN_LUZ(DelayService.class, false, "BRN", "LUZ", "0010000000"),
        LUZ_BAS(DelayService.class, false, "LUZ", "BAS"),
        BAS_LUZ(DelayService.class, false, "BAS", "LUZ"),
        LUZ_ZRH(DelayService.class, false, "LUZ", "ZRH"),
        ZRH_LUZ(DelayService.class, false, "ZRH", "LUZ"),
        LUZ(WeatherService.class, false, "LUZ"),
        TIT(WeatherService.class, false, "TIT"),
        BER(WeatherService.class, false, "BER"),
        ENG(WeatherService.class, false, "ENG"),
        LIFT(TitlisService.class, false, new String[]{}),
        FOEHN(FoehnService.class, true, new String[]{});


        private Class clazz;
        private boolean photo;
        private String[] parameter;

        COMMAND(Class clazz, boolean photo, String... parameter) {
            this.clazz = clazz;
            this.photo = photo;
            this.parameter = parameter;
        }

        public String[] getParameter() {
            return parameter;
        }

        public boolean isPhoto() {
            return photo;
        }
    }


}
