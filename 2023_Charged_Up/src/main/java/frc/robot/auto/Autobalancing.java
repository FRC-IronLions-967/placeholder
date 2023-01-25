package frc.robot.auto;
import com.kauailabs.navx.frc.AHRS;
import frc.robot.IO;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SubsystemsInstance;

public class Autobalancing extends CommandBase {
    
    private boolean autoBalanceXMode;
    private boolean autoBalanceYMode;
    
    private AHRS ahrs;

    private SubsystemsInstance inst;
    private IO io;

    private static final double kOffBalanceAngleThresholdDegrees = 10;
    private static final double kOonBalanceAngleThresholdDegrees  = 5;

    public Autobalancing(){
        ahrs = new AHRS(SPI.Port.kMXP);
        inst = SubsystemsInstance.getInstance(); 
        addRequirements(inst.driveSubsystem);
    }

    public void operatorControl() {
        // while (operatorControl() /*&& isEnabled() add this for pid controller */) {

            double xAxisRate            = io.getDriverController().getRightStickX();
            double yAxisRate            = io.getDriverController().getLeftStickY();
            double pitchAngleDegrees    = ahrs.getPitch();
            double rollAngleDegrees     = ahrs.getRoll();

            if ( !autoBalanceXMode && 
                 (Math.abs(pitchAngleDegrees) >= 
                  Math.abs(kOffBalanceAngleThresholdDegrees))) {
                autoBalanceXMode = true;
            }
            else if ( autoBalanceXMode && 
                      (Math.abs(pitchAngleDegrees) <= 
                       Math.abs(kOonBalanceAngleThresholdDegrees))) {
                autoBalanceXMode = false;
            }
            if ( !autoBalanceYMode && 
                 (Math.abs(pitchAngleDegrees) >= 
                  Math.abs(kOffBalanceAngleThresholdDegrees))) {
                autoBalanceYMode = true;
            }
            else if ( autoBalanceYMode && 
                      (Math.abs(pitchAngleDegrees) <= 
                       Math.abs(kOonBalanceAngleThresholdDegrees))) {
                autoBalanceYMode = false;
            }

            // Control drive system automatically, 
            // driving in reverse direction of pitch/roll angle,
            // with a magnitude based upon the angle

            if ( autoBalanceXMode ) {
                double pitchAngleRadians = pitchAngleDegrees * (Math.PI / 180.0);
                xAxisRate = Math.sin(pitchAngleRadians) * -1;
            }
            if ( autoBalanceYMode ) {
                double rollAngleRadians = rollAngleDegrees * (Math.PI / 180.0);
                yAxisRate = Math.sin(rollAngleRadians) * -1;
            }
            inst.driveSubsystem.move(xAxisRate, yAxisRate);
        }


    // }

}

