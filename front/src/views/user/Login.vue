<template>
  <div>
    <div class="title">登录</div>
    <Form
      ref="formAccount"
      :model="formAccount"
      :rules="ruleAccount"
      label-position="top"
    >
      <FormItem label="账号" prop="name">
        <Input type="text" v-model="formAccount.name">
          <Icon type="ios-person-outline" slot="prepend"></Icon>
        </Input>
      </FormItem>
      <FormItem label="密码" prop="password">
        <Input type="password" v-model="formAccount.password">
          <Icon type="ios-lock-outline" slot="prepend"></Icon>
        </Input>
      </FormItem>
      <FormItem>
        <Button long type="primary" @click="handleSubmit('formAccount')">登录</Button>
      </FormItem>
    </Form>
    <div class="font-center">
        <div><a href="#">忘记密码</a></div>
        <div>尚未拥有账户？<a href="/user/register">注册</a></div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      formAccount: {
        name: "",
        password: "",
      },
      ruleAccount: {
        name: [
          {
            required: true,
            message: "请输入账号",
            trigger: "blur",
          },
        ],
        password: [
          {
            required: true,
            message: "请输入密码",
            trigger: "blur",
          },
        ],
      },
    };
  },
  methods: {
    handleSubmit(name) {
      this.$refs[name].validate((valid) => {
        if (valid) {
          this.$Message.success("Success!");
        } else {
          this.$Message.error("Fail!");
        }
      });
    },
  },
};
</script>

<style>
.title {
  text-align: center;
  border-bottom: 1px solid #e8eaec;
  padding-bottom: 16px;
  margin-bottom: 32px;
  font-size: 16px;
}
.font-center {
  text-align: center;
}
</style>