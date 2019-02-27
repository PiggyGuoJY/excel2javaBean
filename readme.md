# com.tkp.tkpole.starter.utils的使用

# 目录

# 正文

## 1. ECM(集团影像件)
- 配置文件
```yaml
# 使能ECM, 使用默认数据配置ECM环境
_util:
    enableEcm: true

# 如果存在符合条件的具体配置, 使用具体配置数据配置ECM环境, 
# 具体内容参考EcmConfigData和RestConfigData
#_rest: ...
#_ecm: ...
```
- 代码
```java
import com.tkp.tkpole.starter.utils.misc.ecm.Ecm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class XXX{
    @Autowired
    public XXX(Ecm ecm) {this.ecm = ecm;}
    
    private Ecm ecm;
}
```

## 2. EDM(集团邮件)
- 配置文件
```yaml
# 使能EDM, 使用默认数据配置EDM环境
_util:
    enableEdm: true

# 如果存在符合条件的具体配置, 使用具体配置数据配置ECM环境, 
# 具体内容参考EdmConfigData和SoapConfigData
#_soap: ...
#_edm: ...
```
- 代码 
```java
import com.tkp.tkpole.starter.utils.misc.edm.Edm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class XXX{
    @Autowired
    public XXX(Edm edm) {this.edm = edm;}
    
    private Edm edm;
}
```

## 3. OA(集团OA认证)
- 配置文件
```yaml
_util:
  enableOaAuth: true
```
- 代码
```java
import com.tkp.tkpole.starter.utils.misc.oa.OaAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class XXX{
    @Autowired
    public XXX(OaAuthentication oaAuthentication) {this.oaAuthentication = oaAuthentication;}
    
    private OaAuthentication oaAuthentication;
}
```

## 4. SMS(短信服务)
- 配置文件
```yaml
# 使能SMS, 使用默认数据配置SMS环境
_util:
  enableSms: true
# 如果存在符合条件的具体配置, 使用具体配置数据配置ECM环境, 
# 具体内容参考TkSystemConfigData和RestConfigData
 #_rest:
 #_tkSystem:
```
- 代码
```java
import com.tkp.tkpole.starter.utils.misc.sms.MsgSender;
import com.tkp.tkpole.starter.utils.misc.sms.TkpMsg;
import com.tkp.tkpole.starter.utils.misc.sms.TkSms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class XXX{
    @Autowired
    public XXX(
            @Qualifier("MsgSender4TkpMsg") MsgSender tkpMsg, 
            @Qualifier("MsgSender4TkSms")MsgSender tkSms
    ) {
        this.tkpMsg = (TkpMsg)tkpMsg;
        this.tkSms = (TkSms)tkSms;
    }
    
    private TkpMsg tkpMsg;
    private TkSms tkSms;
}
```

## 5. Ftp

## 6. RestApi

## 7. SoapApi

# 附录