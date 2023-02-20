<template>
  <div class="container">
    <header>
      <H2>WELCOME TO ANGEYA'S FILE SHARING PLATFORM</H2>
    </header>
    <div class="main-container">
      <aside>
        <el-upload
            class="upload-demo"
            drag
            :on-success="uploadSuccess"
            action="api/file-service/upload">
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">文件最大为4G</div>
        </el-upload>
        <el-input
            type="textarea"
            :rows="3"
            autosize
            placeholder="请输入内容"
            v-model="tempText">
        </el-input>
        <el-button @click="saveTempText(false)">Save</el-button>
        <el-button @click="saveTempText(true)">Clean</el-button>
      </aside>
      <main>
        <el-table
            :data="fileList"
            height="100%"
            border
            style="width: 100%">
          <el-table-column prop="name" label="File Name" width="200" align="center"></el-table-column>
          <el-table-column prop="size" label="File Size" width="200" align="center">
            <template scope="scope">
              <span>{{ switchSizeUnit(scope.row.size) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="dateTime" label="Create Time" width="300" align="center"></el-table-column>
          <el-table-column prop="" label="Operation" width="300">
            <template scope="scope">
              <el-button @click="downloadFile(scope.row.name)">Download</el-button>
              <el-button @click="deleteFile(scope.row.name)">Delete</el-button>
            </template>
          </el-table-column>
        </el-table>
      </main>
    </div>
    <footer>
      <p>There is nothing in the footer!</p>
    </footer>

    <!--  登录框  -->
    <el-dialog
        title="登 录"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        :show-close="false"
        :visible.sync="showLogin"
        width="30%">
      <el-input v-model="password" placeholder="请输入密码"
                ref="passwordInput"
                type="password"
                :clearable="true"
                @keyup.enter.native="login" ></el-input>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="login">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>

import Constant from "@/js/Constant"
import eventManager from "@/js/eventManager";

const ONE_K = 1024
const ONE_M = ONE_K * ONE_K
const FILE_SIZE_DECIMAL_NUMBER = 2

export default {
  name: "FileSharing",
  data() {
    return {
      tempText: '',
      fileList: [],
      showLogin: true,
      password: ''
    }
  },

  methods: {
    login() {
      this.axios.post('/file-service/login', this.password).then(res => {
        if (res.data) {
          this.afterLogin()
          this.$message({
            message: '登录成功！',
            type: 'success'
          });
        } else {
          this.$message({
            message: '登录失败(密码错误)',
            type: 'error'
          });
        }
      })
    },
    afterLogin() {
      this.showLogin = false
      this.getTempText()
      this.getFileList()
    },
    hasLogin() {
      this.axios.get('/file-service/has-login',).then(res => {
        if (res.data) {
          this.afterLogin()
        } else {
          this.showLogin = true
          this.$nextTick(() => {
            this.$refs.passwordInput.focus()
          })
        }
      })
    },
    switchSizeUnit(size) {
      let result
      if (size > ONE_M) {
        result = `${(size / ONE_M).toFixed(FILE_SIZE_DECIMAL_NUMBER)} MB`
      } else if (size > ONE_K) {
        result = `${(size / ONE_K).toFixed(FILE_SIZE_DECIMAL_NUMBER)} KB`
      } else {
        result = `${size} B`
      }
      return result
    },

    downloadFile(fileName) {
      // 文件下载需要设置responseType为blob
      this.axios.get('/file-service/download-file', {
        responseType: "blob",
        params: {fileName}
      }).then(res => {
        let blob = new Blob([res.data]);
        if (window.navigator && window.navigator.msSaveOrOpenBlob) {
          // IE
          window.navigator.msSaveOrOpenBlob(blob, fileName);
        } else {
          let objectUrl = (window.URL || window.webkitURL).createObjectURL(blob);
          let downloadLink = document.createElement("a");
          downloadLink.style.display = "none";
          downloadLink.href = objectUrl;
          // 下载后文件名
          downloadLink.download = fileName;
          document.body.appendChild(downloadLink);
          downloadLink.click();
          // 下载完成移除元素 // window.location.href = objectUrl
          document.body.removeChild(downloadLink);
          // 只要映射存在，Blob就不能进行垃圾回收，因此一旦不再需要引用，就必须小心撤销URL，释放掉blob对象。
          window.URL.revokeObjectURL(objectUrl);
        }
      })
    },

    uploadSuccess() {
      this.getFileList()
    },

    deleteFile(fileName) {
      this.$confirm(`确认删除文件 ${fileName} 吗?`, '提示', {
        confirmButtonText: 'Yes',
        cancelButtonText: 'No',
        type: 'warning'
      }).then(() => {
        this.axios.delete('/file-service/delete-file', {
          params: {fileName}
        }).then(res => {
          if (res.data) {
            this.getFileList()
          }
          this.showOperateResult(res.data)
        })
      })
    },
    getTempText() {
      this.axios.get('/file-service/temp-text').then(res => {
        this.tempText = res.data
      })
    },

    saveTempText(clean = false) {
      if (clean) {
        this.tempText = ''
      }
      this.axios.put('/file-service/temp-text', {
        content: this.tempText
      }).then(res => {
        // do nothing
      })
    },

    getFileList() {
      this.axios.get('/file-service/file-list').then(res => {
        this.fileList = res.data
      })
    },

    showOperateResult(isSuccess) {
      if (isSuccess) {
        this.$message.success("Operate success")
      } else {
        this.$message.success("Operate failed")
      }
    },
    showLoginDialog() {
      this.showLogin = true
      this.$nextTick(() => {
        this.$refs.passwordInput.focus()
      })
    },
  },
  created() {
    eventManager.addEvent(Constant.GO_LOGIN, this.showLoginDialog)
  },
  mounted() {
    this.hasLogin()
  }
}
</script>

<style scoped lang="stylus">
.container
  background-color aliceblue
  height 100vh
  margin 0 10px

  header
    height 12vh
    line-height 10vh
    border-bottom 1px solid black

  .main-container
    display flex
    height calc(100% - 16vh - 2px)

    aside
      border-right 1px solid black
      width 364px

  footer
    height 4vh
    border-top 1px solid black
</style>