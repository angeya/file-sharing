<template>
  <div class="container">
    <header>
      <H2>WELCOME TO ANGEYA'S BOOK SHARING PLATFORM</H2>
    </header>
    <div class="main-container">
      <aside>
        <el-input
            type="textarea"
            autosize
            placeholder="请输入内容"
            v-model="tempText">
        </el-input>
        <el-button @click="saveTempText">
          save
        </el-button>
        <el-upload
            class="upload-demo"
            drag
            action="https://jsonplaceholder.typicode.com/posts/"
            multiple>
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">文件上传说明</div>
        </el-upload>
      </aside>
      <main>
      </main>
    </div>
    <footer>
      <p>There is nothing in the footer!</p>
    </footer>
  </div>
</template>

<script>
export default {
  name: "FileSharing",
  data() {
    return {
      tempText: ''
    }
  },
  methods: {
    getTempText() {
      this.axios.get('/file-service/temp-text').then(res => {
        this.tempText = res.data
        console.log(res)
      })
    },
    saveTempText() {
      this.axios.put('/file-service/temp-text', {
        content: this.tempText
      }).then(res => {
        console.log(res)
      })
    }
  },
  mounted() {
    this.getTempText()
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
    height calc(100% - 14vh - 2px)
    aside
      border-right 1px solid black
      width 364px
  footer
    height 4vh
    border-top 1px solid black
</style>