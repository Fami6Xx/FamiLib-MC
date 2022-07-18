package me.familib.apis.modules.HoloAPI.types.lines;

import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import me.familib.FamiLib;
import org.bukkit.scheduler.BukkitRunnable;


public abstract class UpdatingLine {
    private final TextLine line;

    private final long period;

    public UpdatingLine(TextLine textLine){
        this.line = textLine;
        this.period = 20;
        start();
    }

    public UpdatingLine(TextLine textLine, long period){
        this.line = textLine;
        this.period = period;
        start();
    }

    public abstract String update();

    private void start(){
        new BukkitRunnable(){
            @Override
            public void run(){
                if(!line.getParent().isDeleted()) {
                    String update = update();
                    if (!line.getText().equals(update)) {
                        line.setText(update);
                    }
                }else {
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(FamiLib.getFamiLib(), 1, period);
    }
}
