(function(){"use strict";var e={4622:function(e,t,i){var o=i(144),s=function(){var e=this,t=e._self._c;return t("div",{attrs:{id:"app"}},[t("FileSharing")],1)},n=[],a=function(){var e=this,t=e._self._c;return t("div",{staticClass:"container"},[t("header",[t("H2",[e._v("WELCOME TO ANGEYA'S FILE SHARING PLATFORM")])],1),t("div",{staticClass:"main-container"},[t("aside",[t("el-upload",{attrs:{drag:"","on-success":e.uploadSuccess,action:e.uploadUrl}},[t("i",{staticClass:"el-icon-upload"}),t("div",{staticClass:"el-upload__text"},[e._v("将文件拖到此处，或"),t("em",[e._v("点击上传")])]),t("div",{staticClass:"el-upload__tip",attrs:{slot:"tip"},slot:"tip"},[e._v("文件最大为 100 M")])]),t("el-input",{staticClass:"temp-text-class",attrs:{type:"textarea",rows:3,autosize:"",placeholder:"请输入内容"},model:{value:e.tempText,callback:function(t){e.tempText=t},expression:"tempText"}}),t("el-button",{on:{click:function(t){return e.saveTempText(!1)}}},[e._v("Save")]),t("el-button",{on:{click:function(t){return e.saveTempText(!0)}}},[e._v("Clean")])],1),t("main",[t("el-table",{staticStyle:{width:"100%"},attrs:{data:e.fileList,height:"100%",border:""}},[t("el-table-column",{attrs:{prop:"name",label:"File Name",width:"200",align:"center"}}),t("el-table-column",{attrs:{prop:"size",label:"File Size",width:"200",align:"center"},scopedSlots:e._u([{key:"default",fn:function(i){return[t("span",[e._v(e._s(e.switchSizeUnit(i.row.size)))])]}}])}),t("el-table-column",{attrs:{prop:"dateTime",label:"Create Time",width:"300",align:"center"}}),t("el-table-column",{attrs:{prop:"",label:"Operation",width:"300"},scopedSlots:e._u([{key:"default",fn:function(i){return[t("el-button",{on:{click:function(t){return e.downloadFile(i.row.name)}}},[e._v("Download")]),t("el-button",{on:{click:function(t){return e.deleteFile(i.row.name)}}},[e._v("Delete")])]}}])})],1)],1)]),e._m(0),t("el-dialog",{attrs:{title:"登 录","close-on-click-modal":!1,"close-on-press-escape":!1,"show-close":!1,visible:e.showLogin,width:"30%"},on:{"update:visible":function(t){e.showLogin=t}}},[t("el-input",{ref:"passwordInput",attrs:{placeholder:"请输入密码",type:"password",clearable:!0},nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.login.apply(null,arguments)}},model:{value:e.password,callback:function(t){e.password=t},expression:"password"}}),t("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[t("el-button",{attrs:{type:"primary"},on:{click:e.login}},[e._v("确 定")])],1)],1)],1)},r=[function(){var e=this,t=e._self._c;return t("footer",[t("p",[e._v("There is nothing in the footer!")])])}],l={GO_LOGIN:"goLogin",LONG_TERM_PASSWORD:"longTermPassword",LONG_TERM_PASSWORD_SIGN:"##"};i(7658);const c=new Map;var u={addEvent(e,t){if(c.has(e))c.get(e).push(t);else{const i=[];i.push(t),c.set(e,i)}},emit(e,t){if(c.has(e)){const i=c.get(e);i.forEach((e=>e(t)))}}};const d=1024,p=d*d,h=2;var f={name:"FileSharing",data(){return{tempText:"",fileList:[],showLogin:!0,password:"",waitDownloadFileMap:new Map,useLongTermPassword:!0}},computed:{uploadUrl(){const e="";return e+"/file-service/upload"}},methods:{login(e=!0){if(!this.password)return void this.$message({message:"请输入密码",type:"warning"});const t=0===this.password.indexOf(l.LONG_TERM_PASSWORD_SIGN);this.password=this.password.replaceAll(l.LONG_TERM_PASSWORD_SIGN,""),this.axios.post("/file-service/login",this.password).then((i=>{i.data?(this.afterLogin(),this.$message({message:"登录成功！",type:"success"}),e&&t&&localStorage.setItem(l.LONG_TERM_PASSWORD,this.password)):this.$message({message:"登录失败(密码错误)",type:"error"})}))},afterLogin(){this.showLogin=!1,this.getTempText(),this.getFileList()},hasLogin(){this.axios.get("/file-service/has-login").then((e=>{e.data?this.afterLogin():(this.showLogin=!0,this.$nextTick((()=>{})),this.autoLogin())}))},autoLogin(){const e=localStorage.getItem(l.LONG_TERM_PASSWORD);e?(this.password=e,this.login(),this.$message({message:"自动登录中...",type:"info"})):this.$refs.passwordInput.focus()},switchSizeUnit(e){let t;return t=e>p?`${(e/p).toFixed(h)} MB`:e>d?`${(e/d).toFixed(h)} KB`:`${e} B`,t},canDownloadThisFile(e){const t=this.waitDownloadFileMap.get(e);return!t&&(this.waitDownloadFileMap.set(e,!0),setTimeout((()=>{this.waitDownloadFileMap.set(e,!1)}),3e4),!0)},downloadFile(e){const t=this.canDownloadThisFile(e);t?this.axios.get("/file-service/download-file",{responseType:"blob",params:{fileName:e}}).then((t=>{let i=new Blob([t.data]);if(window.navigator&&window.navigator.msSaveOrOpenBlob)window.navigator.msSaveOrOpenBlob(i,e);else{let t=(window.URL||window.webkitURL).createObjectURL(i),o=document.createElement("a");o.style.display="none",o.href=t,o.download=e,document.body.appendChild(o),o.click(),document.body.removeChild(o),window.URL.revokeObjectURL(t)}this.$message.success("文件已经在以闪电般的速度下载了，不显示下载进度更有神秘感哦...")})):this.$message.warning("请不要频繁操作，OK？")},uploadSuccess(){this.getFileList()},deleteFile(e){this.$confirm(`确认删除文件 ${e} 吗?`,"提示",{confirmButtonText:"Yes",cancelButtonText:"No",type:"warning"}).then((()=>{this.axios.delete("/file-service/delete-file",{params:{fileName:e}}).then((e=>{e.data&&this.getFileList(),this.showOperateResult(e.data)}))}))},getTempText(){this.axios.get("/file-service/temp-text").then((e=>{this.tempText=e.data}))},saveTempText(e=!1){e&&(this.tempText=""),this.axios.put("/file-service/temp-text",{content:this.tempText}).then((e=>{e&&e.data&&this.showOperateResult(e.data)}))},getFileList(){this.axios.get("/file-service/file-list").then((e=>{this.fileList=e.data}))},showOperateResult(e){e?this.$message.success("Operate success"):this.$message.error("Operate failed")},showLoginDialog(){this.showLogin=!0,this.$nextTick((()=>{this.$refs.passwordInput.focus()}))}},created(){u.addEvent(l.GO_LOGIN,this.showLoginDialog)},mounted(){this.hasLogin()}},g=f,w=i(1001),m=(0,w.Z)(g,a,r,!1,null,"5bef1394",null),v=m.exports,O={name:"App",components:{FileSharing:v}},b=O,_=(0,w.Z)(b,s,n,!1,null,null,null),L=_.exports,T=i(6154),x=i(4720),y=i.n(x);o["default"].config.productionTip=!1;const S=T.Z.create({baseURL:"",crossDomain:!0});S.interceptors.response.use((e=>e.data===l.GO_LOGIN?(u.emit(l.GO_LOGIN),Promise.reject("you should login")):e),(e=>Promise.reject(e.response.data))),o["default"].prototype.axios=S,o["default"].use(y()),new o["default"]({render:e=>e(L)}).$mount("#app")}},t={};function i(o){var s=t[o];if(void 0!==s)return s.exports;var n=t[o]={id:o,loaded:!1,exports:{}};return e[o](n,n.exports,i),n.loaded=!0,n.exports}i.m=e,function(){i.amdO={}}(),function(){var e=[];i.O=function(t,o,s,n){if(!o){var a=1/0;for(u=0;u<e.length;u++){o=e[u][0],s=e[u][1],n=e[u][2];for(var r=!0,l=0;l<o.length;l++)(!1&n||a>=n)&&Object.keys(i.O).every((function(e){return i.O[e](o[l])}))?o.splice(l--,1):(r=!1,n<a&&(a=n));if(r){e.splice(u--,1);var c=s();void 0!==c&&(t=c)}}return t}n=n||0;for(var u=e.length;u>0&&e[u-1][2]>n;u--)e[u]=e[u-1];e[u]=[o,s,n]}}(),function(){i.n=function(e){var t=e&&e.__esModule?function(){return e["default"]}:function(){return e};return i.d(t,{a:t}),t}}(),function(){i.d=function(e,t){for(var o in t)i.o(t,o)&&!i.o(e,o)&&Object.defineProperty(e,o,{enumerable:!0,get:t[o]})}}(),function(){i.g=function(){if("object"===typeof globalThis)return globalThis;try{return this||new Function("return this")()}catch(e){if("object"===typeof window)return window}}()}(),function(){i.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)}}(),function(){i.r=function(e){"undefined"!==typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})}}(),function(){i.nmd=function(e){return e.paths=[],e.children||(e.children=[]),e}}(),function(){var e={143:0};i.O.j=function(t){return 0===e[t]};var t=function(t,o){var s,n,a=o[0],r=o[1],l=o[2],c=0;if(a.some((function(t){return 0!==e[t]}))){for(s in r)i.o(r,s)&&(i.m[s]=r[s]);if(l)var u=l(i)}for(t&&t(o);c<a.length;c++)n=a[c],i.o(e,n)&&e[n]&&e[n][0](),e[n]=0;return i.O(u)},o=self["webpackChunkweb"]=self["webpackChunkweb"]||[];o.forEach(t.bind(null,0)),o.push=t.bind(null,o.push.bind(o))}();var o=i.O(void 0,[998],(function(){return i(4622)}));o=i.O(o)})();
//# sourceMappingURL=app.12d83fc2.js.map