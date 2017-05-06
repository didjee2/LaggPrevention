package com.dbsoftwares.laggprevention.runnable;

import com.dbsoftwares.laggprevention.LaggPrevention;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public abstract class LaggRunnable extends BukkitRunnable {
	
	private LaggRunnable main = this;
	private Boolean running = false;
	
	@Override
	public abstract void run();
	
	public Boolean isRunning(){
		return this.running;
	}
	
	public LaggRunnable clone(){
	    return new LaggRunnable() {
            @Override
            public void run() {
                main.run();
            }
        };
	}

	public LaggRunnable start(){
		if(running){
			this.cancel();
			this.running = false;
		}
		this.runTask(LaggPrevention.getInstance());
		this.running = true;
		return this;
	}
	
	public LaggRunnable startAsync(){
		if(running){
			this.cancel();
			this.running = false;
		}
		this.runTaskAsynchronously(LaggPrevention.getInstance());
		this.running = true;
		return this;
	}
	
	public LaggRunnable startAsync(TimeUnit unit, Integer delay){
		if(running){
			this.cancel();
			this.running = false;
		}
		this.runTaskLaterAsynchronously(LaggPrevention.getInstance(), unit.toSeconds(delay) * 20);
		this.running = true;
		return this;
	}
	
	public LaggRunnable startAsync(TimeUnit unit, Integer delay, Integer repeat){
		if(running){
			this.cancel();
			this.running = false;
		}
		this.runTaskTimerAsynchronously(LaggPrevention.getInstance(), unit.toSeconds(delay) * 20, unit.toSeconds(repeat) * 20);
		this.running = true;
		return this;
	}
	
	public LaggRunnable start(TimeUnit unit, Integer delay){
		if(running){
			this.cancel();
			this.running = false;
		}
		this.runTaskLater(LaggPrevention.getInstance(), unit.toSeconds(delay) * 20);
		this.running = true;
		return this;
	}
	
	public LaggRunnable start(TimeUnit unit, Integer delay, Integer repeat){
		if(running){
			this.cancel();
			this.running = false;
		}
		this.runTaskTimer(LaggPrevention.getInstance(), unit.toSeconds(delay) * 20, unit.toSeconds(repeat) * 20);
		this.running = true;
		return this;
	}
	
	public LaggRunnable start(JavaPlugin instance){
		if(running){
			this.cancel();
			this.running = false;
		}
		this.runTask(instance);
		this.running = true;
		return this;
	}
	
	public LaggRunnable startAsync(JavaPlugin instance){
		if(running){
			this.cancel();
			this.running = false;
		}
		this.runTaskAsynchronously(instance);
		this.running = true;
		return this;
	}
	
	public LaggRunnable startAsync(JavaPlugin instance, TimeUnit unit, Integer delay){
		if(running){
			this.cancel();
			this.running = false;
		}
		this.runTaskLaterAsynchronously(instance, unit.toSeconds(delay) * 20);
		this.running = true;
		return this;
	}
	
	public LaggRunnable startAsync(JavaPlugin instance, TimeUnit unit, Integer delay, Integer repeat){
		if(running){
			this.cancel();
			this.running = false;
		}
		this.runTaskTimerAsynchronously(instance, unit.toSeconds(delay) * 20, unit.toSeconds(repeat) * 20);
		this.running = true;
		return this;
	}
	
	public LaggRunnable start(JavaPlugin instance, TimeUnit unit, Integer delay){
		if(running){
			this.cancel();
			this.running = false;
		}
		this.runTaskLater(instance, unit.toSeconds(delay) * 20);
		this.running = true;
		return this;
	}
	
	public LaggRunnable start(JavaPlugin instance, TimeUnit unit, Integer delay, Integer repeat){
		if(running){
			this.cancel();
			this.running = false;
		}
		this.runTaskTimer(instance, unit.toSeconds(delay) * 20, unit.toSeconds(repeat) * 20);
		this.running = true;
		return this;
	}
}
