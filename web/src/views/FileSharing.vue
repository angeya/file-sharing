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
          <div class="el-upload__tip" slot="tip">文件上传说明</div>
        </el-upload>
        <el-input
            type="textarea"
            autosize
            placeholder="请输入内容"
            v-model="tempText">
        </el-input>
        <el-button @click="saveTempText">Save</el-button>
        <el-button @click="saveTempText">Clean</el-button>
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
              <span>{{switchSizeUnit(scope.row.size)}}</span>
            </template>
          </el-table-column>
          <el-table-column prop="dateTime" label="Create Time" width="300"  align="center"></el-table-column>
          <el-table-column prop="" label="Operation" width="300">
            <template scope="scope">
              <el-button @click="downloadFile(scope.row.name)">Download</el-button>
              <el-button @click="deleteFile">Delete</el-button>
            </template>
          </el-table-column>
        </el-table>
      </main>
    </div>
    <footer>
      <p>There is nothing in the footer!</p>
    </footer>
  </div>
</template>

<script>

const ONE_K = 1024
const ONE_M = ONE_K * ONE_K
const FILE_SIZE_DECIMAL_NUMBER = 2

export default {
  name: "FileSharing",
  data() {
    return {
      tempText: '',
      fileList: []
    }
  },
  methods: {
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
        params: { fileName } } ).then(res => {
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
    deleteFile() {
      this.axios.delete('/file-service/file').then(res => {
        console.log(res)
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
    }
  },
  mounted() {
    this.getTempText()
    this.getFileList()
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