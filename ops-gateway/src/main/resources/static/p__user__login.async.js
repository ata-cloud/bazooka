(window.webpackJsonp=window.webpackJsonp||[]).push([[16],{"7ncd":function(e,t,n){e.exports={main:"antd-pro-pages-user-login-main",icon:"antd-pro-pages-user-login-icon",other:"antd-pro-pages-user-login-other",register:"antd-pro-pages-user-login-register"}},JAxp:function(e,t,n){e.exports={login:"antd-pro-components-login-index-login",getCaptcha:"antd-pro-components-login-index-getCaptcha",icon:"antd-pro-components-login-index-icon",other:"antd-pro-components-login-index-other",register:"antd-pro-components-login-index-register",prefixIcon:"antd-pro-components-login-index-prefixIcon",submit:"antd-pro-components-login-index-submit"}},YkAm:function(e,t,n){e.exports={"ant-alert":"ant-alert","ant-alert-no-icon":"ant-alert-no-icon","ant-alert-closable":"ant-alert-closable","ant-alert-icon":"ant-alert-icon","ant-alert-description":"ant-alert-description","ant-alert-success":"ant-alert-success","ant-alert-info":"ant-alert-info","ant-alert-warning":"ant-alert-warning","ant-alert-error":"ant-alert-error","ant-alert-close-icon":"ant-alert-close-icon","anticon-close":"anticon-close","ant-alert-close-text":"ant-alert-close-text","ant-alert-with-description":"ant-alert-with-description","ant-alert-message":"ant-alert-message","ant-alert-close":"ant-alert-close","ant-alert-slide-up-leave":"ant-alert-slide-up-leave",antAlertSlideUpOut:"antAlertSlideUpOut","ant-alert-banner":"ant-alert-banner",antAlertSlideUpIn:"antAlertSlideUpIn"}},ZZkd:function(e,t,n){"use strict";n.r(t);n("cIOH"),n("YkAm");var a=n("q1tI"),r=n.n(a),o=n("i8i4"),c=n("MFj2"),i=n("TSYQ"),s=n.n(i),l=n("CtXQ"),u=n("wEI+");var p=n("6CfX");function m(e){return(m="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(e){return typeof e}:function(e){return e&&"function"==typeof Symbol&&e.constructor===Symbol&&e!==Symbol.prototype?"symbol":typeof e})(e)}function f(){return(f=Object.assign||function(e){for(var t=1;t<arguments.length;t++){var n=arguments[t];for(var a in n)Object.prototype.hasOwnProperty.call(n,a)&&(e[a]=n[a])}return e}).apply(this,arguments)}function d(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function h(e,t){for(var n=0;n<t.length;n++){var a=t[n];a.enumerable=a.enumerable||!1,a.configurable=!0,"value"in a&&(a.writable=!0),Object.defineProperty(e,a.key,a)}}function g(e){return(g=Object.setPrototypeOf?Object.getPrototypeOf:function(e){return e.__proto__||Object.getPrototypeOf(e)})(e)}function b(e){if(void 0===e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return e}function v(e,t){return(v=Object.setPrototypeOf||function(e,t){return e.__proto__=t,e})(e,t)}function y(){}var E=function(e){function t(e){var n,r,i;return function(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}(this,t),r=this,i=g(t).call(this,e),(n=!i||"object"!==m(i)&&"function"!=typeof i?b(r):i).handleClose=function(e){e.preventDefault();var t=o.findDOMNode(b(n));t.style.height="".concat(t.offsetHeight,"px"),t.style.height="".concat(t.offsetHeight,"px"),n.setState({closing:!1}),(n.props.onClose||y)(e)},n.animationEnd=function(){n.setState({closed:!0,closing:!0}),(n.props.afterClose||y)()},n.renderAlert=function(e){var t,r,o=e.getPrefixCls,i=n.props,u=i.description,p=i.prefixCls,m=i.message,h=i.closeText,g=i.banner,b=i.className,v=void 0===b?"":b,y=i.style,E=i.icon,C=n.props,w=C.closable,x=C.type,S=C.showIcon,N=C.iconType,O=o("alert",p);S=!(!g||void 0!==S)||S,x=g&&void 0===x?"warning":x||"info";var k="filled";if(!N){switch(x){case"success":N="check-circle";break;case"info":N="info-circle";break;case"error":N="close-circle";break;case"warning":N="exclamation-circle";break;default:N="default"}u&&(k="outlined")}h&&(w=!0);var T,P=s()(O,"".concat(O,"-").concat(x),(d(t={},"".concat(O,"-close"),!n.state.closing),d(t,"".concat(O,"-with-description"),!!u),d(t,"".concat(O,"-no-icon"),!S),d(t,"".concat(O,"-banner"),!!g),d(t,"".concat(O,"-closable"),w),t),v),j=w?a.createElement("button",{type:"button",onClick:n.handleClose,className:"".concat(O,"-close-icon"),tabIndex:0},h?a.createElement("span",{className:"".concat(O,"-close-text")},h):a.createElement(l.a,{type:"close"})):null,I=(T=n.props,Object.keys(T).reduce(function(e,t){return"data-"!==t.substr(0,5)&&"aria-"!==t.substr(0,5)&&"role"!==t||"data-__"===t.substr(0,7)||(e[t]=T[t]),e},{})),A=E&&(a.isValidElement(E)?a.cloneElement(E,{className:s()((r={},d(r,E.props.className,E.props.className),d(r,"".concat(O,"-icon"),!0),r))}):a.createElement("span",{className:"".concat(O,"-icon")},E))||a.createElement(l.a,{className:"".concat(O,"-icon"),type:N,theme:k});return n.state.closed?null:a.createElement(c.a,{component:"",showProp:"data-show",transitionName:"".concat(O,"-slide-up"),onEnd:n.animationEnd},a.createElement("div",f({"data-show":n.state.closing,className:P,style:y},I),S?A:null,a.createElement("span",{className:"".concat(O,"-message")},m),a.createElement("span",{className:"".concat(O,"-description")},u),j))},Object(p.a)(!("iconType"in e),"Alert","`iconType` is deprecated. Please use `icon` instead."),n.state={closing:!0,closed:!1},n}var n,r,i;return function(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function");e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,writable:!0,configurable:!0}}),t&&v(e,t)}(t,a["Component"]),n=t,(r=[{key:"render",value:function(){return a.createElement(u.a,null,this.renderAlert)}}])&&h(n.prototype,r),i&&h(n,i),t}(),C=n("p0pE"),w=n.n(C),x=n("2Taf"),S=n.n(x),N=n("vZ4D"),O=n.n(N),k=n("l4Ni"),T=n.n(k),P=n("ujKo"),j=n.n(P),I=n("MhPg"),A=n.n(I),D=n("MuoO"),M=n("LLXN"),U=(n("y8nQ"),n("Vl3Y")),F=(n("Znn+"),n("ZTPi")),_=n("gWZ8"),L=n.n(_),G=(n("17x9"),n("14J3"),n("BMrR")),q=(n("+L6B"),n("2/Rp")),B=(n("jCWc"),n("kPKH")),Z=(n("5NDa"),n("5rEg")),z=n("jehZ"),K=n.n(z),V=n("Y/ft"),J=n.n(V),Y=n("BGR+"),H=n("JAxp"),R=n.n(H),W=(n("Pwec"),{UserName:{props:{size:"large",id:"userName",prefix:r.a.createElement(l.a,{type:"user",className:R.a.prefixIcon}),placeholder:"admin"},rules:[{required:!0,message:"请输入用户名"}]},Password:{props:{size:"large",prefix:r.a.createElement(l.a,{type:"lock",className:R.a.prefixIcon}),type:"password",id:"password",placeholder:"888888"},rules:[{required:!0,message:"请输入密码"}]},Mobile:{props:{size:"large",prefix:r.a.createElement(l.a,{type:"mobile",className:R.a.prefixIcon}),placeholder:"mobile number"},rules:[{required:!0,message:"Please enter mobile number!"},{pattern:/^1\d{10}$/,message:"Wrong mobile number format!"}]},Captcha:{props:{size:"large",prefix:r.a.createElement(l.a,{type:"mail",className:R.a.prefixIcon}),placeholder:"captcha"},rules:[{required:!0,message:"Please enter Captcha!"}]}}),Q=Object(a.createContext)(),X=U.a.Item,$=function(e){function t(e){var n;return S()(this,t),(n=T()(this,j()(t).call(this,e))).onGetCaptcha=function(){var e=n.props.onGetCaptcha,t=e?e():null;!1!==t&&(t instanceof Promise?t.then(n.runGetCaptchaCountDown):n.runGetCaptchaCountDown())},n.getFormItemOptions=function(e){var t=e.onChange,n=e.defaultValue,a=e.customprops,r={rules:e.rules||a.rules};return t&&(r.onChange=t),n&&(r.initialValue=n),r},n.runGetCaptchaCountDown=function(){var e=n.props.countDown||59;n.setState({count:e}),n.interval=setInterval(function(){e-=1,n.setState({count:e}),0===e&&clearInterval(n.interval)},1e3)},n.state={count:0},n}return A()(t,e),O()(t,[{key:"componentDidMount",value:function(){var e=this.props,t=e.updateActive,n=e.name;t&&t(n)}},{key:"componentWillUnmount",value:function(){clearInterval(this.interval)}},{key:"render",value:function(){var e=this.state.count,t=this.props.form.getFieldDecorator,n=this.props,a=(n.onChange,n.customprops),o=(n.defaultValue,n.rules,n.name),c=n.getCaptchaButtonText,i=n.getCaptchaSecondText,s=(n.updateActive,n.type),l=J()(n,["onChange","customprops","defaultValue","rules","name","getCaptchaButtonText","getCaptchaSecondText","updateActive","type"]),u=this.getFormItemOptions(this.props),p=l||{};if("Captcha"===s){var m=Object(Y.default)(p,["onGetCaptcha","countDown"]);return r.a.createElement(X,null,r.a.createElement(G.a,{gutter:8},r.a.createElement(B.a,{span:16},t(o,u)(r.a.createElement(Z.a,K()({},a,m)))),r.a.createElement(B.a,{span:8},r.a.createElement(q.a,{disabled:e,className:R.a.getCaptcha,size:"large",onClick:this.onGetCaptcha},e?"".concat(e," ").concat(i):c))))}return r.a.createElement(X,null,t(o,u)(r.a.createElement(Z.a,K()({},a,p))))}}]),t}(a.Component);$.defaultProps={getCaptchaButtonText:Object(M.formatMessage)({id:"form.captcha"}),getCaptchaSecondText:Object(M.formatMessage)({id:"form.captcha.second"})};var ee={};Object.keys(W).forEach(function(e){var t=W[e];ee[e]=function(n){return r.a.createElement(Q.Consumer,null,function(a){return r.a.createElement($,K()({customprops:t.props,rules:t.rules},n,{type:e,updateActive:a.updateActive,form:a.form}))})}});var te,ne=ee,ae=F.a.TabPane,re=(te=0,function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"";return te+=1,"".concat(e).concat(te)}),oe=function(e){function t(e){var n;return S()(this,t),(n=T()(this,j()(t).call(this,e))).uniqueId=re("login-tab-"),n}return A()(t,e),O()(t,[{key:"componentDidMount",value:function(){this.props.tabUtil.addTab(this.uniqueId)}},{key:"render",value:function(){var e=this.props.children;return r.a.createElement(ae,this.props,e)}}]),t}(a.Component),ce=function(e){return r.a.createElement(Q.Consumer,null,function(t){return r.a.createElement(oe,K()({tabUtil:t.tabUtil},e))})};ce.typeName="LoginTab";var ie=ce,se=U.a.Item,le=function(e){var t=e.className,n=J()(e,["className"]),a=s()(R.a.submit,t);return r.a.createElement(se,null,r.a.createElement(q.a,K()({size:"large",className:a,type:"primary",htmlType:"submit"},n)))},ue=function(e){function t(e){var n;return S()(this,t),(n=T()(this,j()(t).call(this,e))).onSwitch=function(e){n.setState({type:e}),(0,n.props.onTabChange)(e)},n.getContext=function(){var e=n.state.tabs;return{tabUtil:{addTab:function(t){n.setState({tabs:[].concat(L()(e),[t])})},removeTab:function(t){n.setState({tabs:e.filter(function(e){return e!==t})})}},form:n.props.form,updateActive:function(e){var t=n.state,a=t.type,r=t.active;r[a]?r[a].push(e):r[a]=[e],n.setState({active:r})}}},n.handleSubmit=function(e){e.preventDefault();var t=n.state,a=t.active,r=t.type,o=n.props,c=o.form,i=o.onSubmit,s=a[r];c.validateFields(s,{force:!0},function(e,t){i(e,t)})},n.state={type:e.defaultActiveKey,tabs:[],active:{}},n}return A()(t,e),O()(t,[{key:"render",value:function(){var e=this.props,t=e.className,n=e.children,a=this.state,o=a.type,c=a.tabs,i=[],l=[];return r.a.Children.forEach(n,function(e){e&&("LoginTab"===e.type.typeName?i.push(e):l.push(e))}),r.a.createElement(Q.Provider,{value:this.getContext()},r.a.createElement("div",{className:s()(t,R.a.login)},r.a.createElement(U.a,{onSubmit:this.handleSubmit},c.length?r.a.createElement(r.a.Fragment,null,r.a.createElement(F.a,{animated:!1,className:R.a.tabs,activeKey:o,onChange:this.onSwitch},i),l):L()(n))))}}]),t}(a.Component);ue.defaultProps={className:"",defaultActiveKey:"",onTabChange:function(){},onSubmit:function(){}},ue.Tab=ie,ue.Submit=le,Object.keys(ne).forEach(function(e){ue[e]=ne[e]});var pe=U.a.create()(ue),me=n("7ncd"),fe=n.n(me),de=(pe.Tab,pe.UserName),he=pe.Password,ge=(pe.Mobile,pe.Captcha,pe.Submit),be=function(e){function t(){var e,n;S()(this,t);for(var a=arguments.length,o=new Array(a),c=0;c<a;c++)o[c]=arguments[c];return(n=T()(this,(e=j()(t)).call.apply(e,[this].concat(o)))).state={type:"account",autoLogin:!0},n.handleSubmit=function(e,t){n.state.type;e||(0,n.props.dispatch)({type:"login/login",payload:w()({},t)})},n.changeAutoLogin=function(e){n.setState({autoLogin:e.target.checked})},n.renderMessage=function(e){return r.a.createElement(E,{style:{marginBottom:24},message:e,type:"error",showIcon:!0})},n}return A()(t,e),O()(t,[{key:"render",value:function(){var e=this,t=this.props,n=(t.login,t.submitting),a=this.state,o=a.type;a.autoLogin;return r.a.createElement("div",{className:fe.a.main},r.a.createElement(pe,{defaultActiveKey:o,onSubmit:this.handleSubmit,ref:function(t){e.loginForm=t}},r.a.createElement("div",null,r.a.createElement(de,{name:"username",placeholder:"请输入用户名"}),r.a.createElement(he,{name:"password",placeholder:"请输入密码",onPressEnter:function(){return e.loginForm.validateFields(e.handleSubmit)}})),r.a.createElement(ge,{loading:n},r.a.createElement(M.FormattedMessage,{id:"app.login.login"}))))}}]),t}(a.Component);t.default=Object(D.connect)(function(e){e.login;return{submitting:e.loading.effects["login/login"]}})(be)}}]);