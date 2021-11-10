<template>
  <div>
    <div class="title">注册</div>
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
        <Input
          type="password"
          v-model="formAccount.password"
          placeholder="至少6位密码"
        >
          <Icon type="ios-lock-outline" slot="prepend"></Icon>
        </Input>
      </FormItem>
      <FormItem label="确认密码" prop="passwordCheck">
        <Input type="password" v-model="formAccount.passwordCheck">
          <Icon type="ios-lock-outline" slot="prepend"></Icon>
        </Input>
      </FormItem>
      <FormItem>
        <Button long type="primary" @click="handleSubmit('formAccount')">注册</Button>
      </FormItem>
    </Form>
    <div class="font-center">已经拥有账户？<a href="/user/login">登录</a></div>
  </div>
</template>

<script>
export default {
  data() {
    const validateName = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('账号不能为空'));
      } else {
        callback();
      }
    };
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('密码不能为空'));
      } else {
        if (this.formAccount.passwordCheck !== '') {
          // 对第二个密码框单独验证
          this.$refs.formAccount.validateField('passwordCheck');
        }
        callback();
      }
    };
    const validatePassCheck = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入确认密码'));
      } else if (value !== this.formAccount.password) {
        callback(new Error('两次输入的密码不匹配'));
      } else {
        callback();
      }
    };
    return {
      formAccount: {
        name: '',
        password: '',
        passwordCheck: '',
      },
      ruleAccount: {
        name: [
          {
            validator: validateName, trigger: 'blur',
          },
        ],
        password: [
          { validator: validatePass, trigger: 'blur' },
          {
            type: 'string',
            min: 6,
            message: '密码不能少于6位',
            trigger: 'change',
          },
        ],
        passwordCheck: [{ validator: validatePassCheck, trigger: 'change' }],
      },
    };
  },
  methods: {
    handleSubmit(name) {
      this.$refs[name].validate((valid) => {
        if (valid) {
          this.$Message.success('Success');
        } else {
          this.$Message.error('Fail');
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