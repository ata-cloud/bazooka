(window.webpackJsonp=window.webpackJsonp||[]).push([[9],{ouTy:function(e,t,a){"use strict";a.r(t);a("y8nQ");var n=a("Vl3Y"),c=(a("T2oS"),a("W9HT")),l=(a("IzEo"),a("bx4M")),r=(a("Pwec"),a("CtXQ")),s=(a("R9oj"),a("ECub")),o=(a("MXD1"),a("CFYs")),m=(a("14J3"),a("BMrR")),i=(a("jCWc"),a("kPKH")),u=(a("+L6B"),a("2/Rp")),d=a("2Taf"),E=a.n(d),p=a("vZ4D"),v=a.n(p),y=a("l4Ni"),g=a.n(y),f=a("ujKo"),h=a.n(f),N=a("MhPg"),x=a.n(N),k=(a("tU7J"),a("wFql")),b=a("q1tI"),C=a.n(b),j=a("y1Nh"),w=a("MuoO"),I=a("XfOM"),L=a.n(I),O=a("iJd5"),R=a("+n12"),T=a("3a4m"),M=a.n(T),H=(k.a.Title,k.a.Text),Q=function(e){function t(e){var a;return E()(this,t),(a=g()(this,h()(t).call(this,e))).onFetchList=function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{pageSize:100};(0,a.props.dispatch)({type:"cluster/clusterList",payload:e})},a.onRaload=function(e){e&&e.stopPropagation(),a.onFetchList()},a.onRouteTo=function(e,t){a.props.history.push({pathname:e,query:t||{}})},a.onAdd=function(){M.a.push("/cluster/add")},a.state={resourceType:[{name:"CPU",value:"",unit:"S"},{name:"磁盘",value:""},{name:"内存",value:""}]},a}return x()(t,e),v()(t,[{key:"componentDidMount",value:function(){Object(R.f)()||M.a.replace("/exception403"),this.onFetchList()}},{key:"renderTitle",value:function(){return C.a.createElement("div",null,C.a.createElement("p",{className:L.a.marginB},"通过Mesos集群、Kubernetes集群或者独立节点的方式，管理物理机、虚拟机、云主机等各类计算资源"),C.a.createElement(m.a,{type:"flex",justify:"space-between",align:"middle"},C.a.createElement(i.a,null,Object(R.f)()||projectListAdmin.length?C.a.createElement(u.a,{type:"primary",onClick:this.onAdd},"+ 新增资源"):null)))}},{key:"renderResource",value:function(e){return C.a.createElement("div",{className:"".concat(L.a.flexCenter),style:{margin:"10px 20px"}},C.a.createElement("div",null,e.name),C.a.createElement("div",{className:"".concat(L.a.flex1," ").concat(L.a.processLR)},C.a.createElement(o.a,{percent:50,showInfo:!1})),C.a.createElement("div",null,"50%（135 / 144 Shares）"))}},{key:"renderList",value:function(){var e=this,t=(this.state.resourceType,this.props),a=t.list,n=t.loading;return C.a.createElement(m.a,{gutter:24,type:"flex"},a&&a.rows&&!a.rows.length&&!n&&C.a.createElement(s.a,{image:s.a.PRESENTED_IMAGE_SIMPLE}),a&&a.rows&&a.rows.length&&a.rows.map(function(t,a){return C.a.createElement(i.a,{md:12,key:a,className:L.a.marginB,sm:24},C.a.createElement(l.a,{title:C.a.createElement("strong",null,t.name,C.a.createElement(H,{type:"secondary"},"（",O.d[t.type],"）")),hoverable:!0,extra:C.a.createElement(r.a,{type:"reload",onClick:function(t){return e.onRaload(t)}}),onClick:function(){e.onRouteTo("/cluster/detail",{clusterId:t.clusterId})}},C.a.createElement("div",null,"2"!==t.type?C.a.createElement("div",{className:L.a.flex},C.a.createElement("div",{className:L.a.clusterItem},C.a.createElement(H,{strong:!0},"状态"),O.c[t.status]&&C.a.createElement(b.Fragment,null,C.a.createElement("div",{className:L.a.midHeight},C.a.createElement(r.a,{type:O.c[t.status].icon,style:{fontSize:30,color:O.c[t.status].colorValue}})),C.a.createElement(H,{type:"secondary"},O.c[t.status].text))),C.a.createElement("div",{className:L.a.clusterItem},C.a.createElement(H,{strong:!0},"环境"),C.a.createElement("div",{className:L.a.midHeight},C.a.createElement("strong",null,t.envQuantity))),C.a.createElement("div",{className:L.a.clusterItem},C.a.createElement(H,{strong:!0},"主机"),C.a.createElement("div",{className:L.a.midHeight},C.a.createElement("span",{className:L.a.textSuccess},t.normalNodeQuantity),C.a.createElement("span",null,"/"),C.a.createElement("strong",null,t.nodeQuantity)),C.a.createElement(H,{type:"secondary"},"正常/所有")),C.a.createElement("div",{className:L.a.clusterItem},C.a.createElement(H,{strong:!0},"容器"),C.a.createElement("div",{className:L.a.midHeight},C.a.createElement("span",{className:L.a.textSuccess},t.runningServiceQuantity||0),C.a.createElement("span",null,"/"),C.a.createElement("strong",null,t.serviceQuantity||0)),C.a.createElement(H,{type:"secondary"},"运行中/所有容器"))):C.a.createElement("div",{className:L.a.flex},C.a.createElement("div",{className:L.a.clusterItem},C.a.createElement(H,{strong:!0},"环境"),C.a.createElement("div",{className:L.a.midHeight},C.a.createElement("strong",null,t.envQuantity))),C.a.createElement("div",{className:L.a.clusterItem},C.a.createElement(H,{strong:!0},"主机"),C.a.createElement("div",{className:L.a.midHeight},C.a.createElement("strong",null,t.nodeQuantity))),C.a.createElement("div",{className:L.a.clusterItem},C.a.createElement(H,{strong:!0},"容器"),C.a.createElement("div",{className:L.a.midHeight},C.a.createElement("strong",null,t.serviceQuantity||0)))),C.a.createElement("div",{className:L.a.marginT},C.a.createElement("p",null,"环境资源（已分隔给各环境的资源 / 集群总资源）"),C.a.createElement("div",{className:"".concat(L.a.flexCenter),style:{margin:"10px 20px"}},C.a.createElement("div",null,"CPU"),C.a.createElement("div",{className:"".concat(L.a.flex1," ").concat(L.a.processLR)},C.a.createElement(o.a,{percent:Object(R.c)(t.envCpu,t.cpu),showInfo:!1,strokeColor:"#1890ff"})),C.a.createElement("div",{className:L.a.rightBlock},"".concat(Object(R.c)(t.envCpu,t.cpu),"%（").concat(t.envCpu," / ").concat(t.cpu||0," Core）"))),C.a.createElement("div",{className:"".concat(L.a.flexCenter),style:{margin:"10px 20px"}},C.a.createElement("div",null,"内存"),C.a.createElement("div",{className:"".concat(L.a.flex1," ").concat(L.a.processLR)},C.a.createElement(o.a,{percent:Object(R.c)(t.envMemory,t.memory),showInfo:!1,strokeColor:"#1890ff"})),C.a.createElement("div",{className:L.a.rightBlock},"".concat(Object(R.c)(t.envMemory,t.memory),"%（").concat(Object(R.a)(t.envMemory)," / ").concat(Object(R.a)(t.memory)," GiB）"))),C.a.createElement("div",{className:"".concat(L.a.flexCenter),style:{margin:"10px 20px"}},"2"!==t.type?C.a.createElement(b.Fragment,null,C.a.createElement("div",null,"磁盘"),C.a.createElement("div",{className:"".concat(L.a.flex1," ").concat(L.a.processLR)},C.a.createElement(o.a,{percent:Object(R.c)(t.envDisk,t.disk),showInfo:!1,strokeColor:"#1890ff"})),C.a.createElement("div",{className:L.a.rightBlock},"".concat(Object(R.c)(t.envDisk,t.disk),"%（").concat(Object(R.a)(t.envDisk)," / ").concat(Object(R.a)(t.disk)," GiB）"))):C.a.createElement("div",{className:"".concat(L.a.flex1," ").concat(L.a.processLR),style:{visibility:"hidden"}},C.a.createElement(o.a,{percent:0})))))))}))}},{key:"render",value:function(){var e=this.props.loading;return C.a.createElement(j.PageHeaderWrapper,{content:this.renderTitle()},C.a.createElement(c.a,{spinning:e},this.renderList()))}}]),t}(C.a.Component);t.default=n.a.create()(Object(w.connect)(function(e){var t=e.cluster,a=e.loading;return{list:t.clusterList,loading:a.effects["cluster/clusterList"]}})(Q))}}]);