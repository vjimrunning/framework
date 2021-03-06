/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.demo.bean;

import com.hbasesoft.rule.plugin.statemachine.StateFlowBean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年1月31日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.test.rule.file.bean <br>
 */
@ToString
@Getter
@Setter
public class FlowBean implements StateFlowBean {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1837859786785097538L;

    private String transId;

    private String lastComponent;

}
