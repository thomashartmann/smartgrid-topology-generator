package smartgridcomm

import org.kevoree.modeling.api.aspect

/**
 * Created by thomas on 5/12/14.
 */

public aspect trait SmartMeterAspect : SmartMeter {
    override fun isRepeater() : Boolean {
        throw Exception("Not implemented yet !");
    }
}