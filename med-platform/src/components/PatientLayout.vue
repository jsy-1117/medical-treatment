<template>
  <div class="patient-layout">
    <header class="pl-header">
      <div class="pl-header-inner">
        <div class="pl-brand">
          <div class="pl-mark">+</div>
          <span>智慧医疗平台</span>
        </div>
        <nav class="pl-nav">
          <router-link to="/">首页</router-link>
          <router-link to="/book">预约挂号</router-link>
          <router-link to="/user/appointments">我的挂号</router-link>
          <router-link to="/user/home">个人中心</router-link>
        </nav>
        <el-button type="primary" plain round icon="HomeFilled" size="small" @click="$router.push('/')">
          返回首页
        </el-button>
      </div>
    </header>
    <main class="pl-content">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
.patient-layout {
  min-height: 100vh;
  background: #f7f8f5;
  background-image:
    linear-gradient(rgba(26,138,122,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(26,138,122,0.03) 1px, transparent 1px);
  background-size: 40px 40px;
  font-family: 'DM Sans', 'Noto Sans SC', -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  color: #1a2a2a;
  -webkit-font-smoothing: antialiased;
}

.pl-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(247, 248, 245, 0.88);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid #eef0ec;
}

.pl-header-inner {
  max-width: 1060px;
  margin: 0 auto;
  height: 60px;
  padding: 0 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.pl-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.pl-mark {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  background: linear-gradient(135deg, #1a8a7a, #2db8a0);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 15px;
  font-weight: 700;
}

.pl-brand span {
  font-size: 16px;
  font-weight: 600;
  letter-spacing: -0.01em;
  color: #1a2a2a;
}

.pl-nav {
  display: flex;
  gap: 4px;
  align-items: center;
}

.pl-nav a {
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  color: #5a6a6a;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.2s;
}

.pl-nav a:hover {
  color: #1a8a7a;
  background: rgba(26, 138, 122, 0.06);
}

.pl-nav a.router-link-exact-active {
  color: #1a8a7a;
  background: rgba(26, 138, 122, 0.08);
  font-weight: 600;
}

.pl-content {
  max-width: 1060px;
  margin: 0 auto;
  padding: 32px 28px 80px;
}

@media (max-width: 768px) {
  .pl-header-inner {
    padding: 12px 16px;
    flex-wrap: wrap;
    height: auto;
    min-height: 56px;
  }

  .pl-nav {
    order: 3;
    width: 100%;
    gap: 0;
  }

  .pl-nav a {
    flex: 1;
    text-align: center;
    font-size: 13px;
    padding: 6px 8px;
  }

  .pl-content {
    padding: 20px 16px 60px;
  }
}
</style>
