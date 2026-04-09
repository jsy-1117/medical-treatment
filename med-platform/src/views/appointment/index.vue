<template>
    <div class="page-container">
        <div class="page-header">
            <div class="header-left">
                <h2 class="page-title">挂号管理</h2>
                <el-breadcrumb separator="/">
                    <el-breadcrumb-item>首页</el-breadcrumb-item>
                    <el-breadcrumb-item>业务中心</el-breadcrumb-item>
                    <el-breadcrumb-item>挂号管理</el-breadcrumb-item>
                </el-breadcrumb>
            </div>
            <div class="header-right">
                <el-button icon="Back" plain @click="goHome">返回门户</el-button>
            </div>
        </div>

        <div class="main-content">
            <el-card class="search-card" shadow="never">
                <el-form :model="queryForm" inline class="search-form">
                    <el-form-item label="挂号状态">
                        <el-select v-model="queryForm.status" placeholder="全部状态" clearable style="width: 160px">
                            <el-option label="待就诊" :value="0" />
                            <el-option label="已完成" :value="1" />
                            <el-option label="已取消" :value="2" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="患者ID">
                        <el-input-number v-model="queryForm.patientId" :min="1" placeholder="ID查询"
                            controls-position="right" style="width: 140px" />
                    </el-form-item>
                    <el-form-item label="医生ID">
                        <el-input-number v-model="queryForm.doctorId" :min="1" placeholder="ID查询"
                            controls-position="right" style="width: 140px" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" icon="Search" @click="handleSearch">查询</el-button>
                        <el-button icon="Refresh" @click="handleReset">重置</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <el-card class="table-card" shadow="never">
                <div class="table-toolbar">
                    <div class="left-tools">
                        <el-icon>
                            <Tickets />
                        </el-icon>
                        <span>挂号列表 <span class="text-gray">({{ total }}条)</span></span>
                    </div>
                </div>

                <el-table :data="tableData" v-loading="loading" stripe style="width: 100%"
                    :header-cell-style="{ background: '#f8fafc', color: '#64748b' }">
                    <el-table-column prop="id" label="挂号" width="100" align="center" fixed="left">
                        <template #default="{ row }">
                            <span class="mono-font">#{{ row.id }}</span>
                        </template>
                    </el-table-column>

                    <el-table-column label="就诊患者" min-width="160">
                        <template #default="{ row }">
                            <div class="user-cell">
                                <el-avatar :size="32" class="avatar patient-bg" icon="User">
                                    {{ row.patientName ? row.patientName.substring(0, 1) : 'P' }}
                                </el-avatar>
                                <div class="info">
                                    <div class="name">{{ row.patientName || '未知患者' }}</div>
                                    <div class="sub-text">ID: {{ row.patientId }}</div>
                                </div>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="预约医生" min-width="160">
                        <template #default="{ row }">
                            <div class="user-cell">
                                <el-avatar :size="32" class="avatar doctor-bg" icon="FirstAidKit">
                                    {{ row.doctorName ? row.doctorName.substring(0, 1) : 'D' }}
                                </el-avatar>
                                <div class="info">
                                    <div class="name">{{ row.doctorName || '未知医生' }}</div>
                                    <div class="sub-text">ID: {{ row.doctorId }}</div>
                                </div>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="就诊时间" width="180">
                        <template #default="{ row }">
                            <div class="time-cell">
                                <span>{{ row.workDate }}</span>
                                <el-tag size="small" :type="row.shiftType === 1 ? 'warning' : 'info'" effect="light">
                                    {{ row.shiftType === 1 ? '上午' : '下午' }}
                                </el-tag>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column label="当前状态" width="120">
                        <template #default="{ row }">
                            <el-tag :type="getStatusType(row.status)" effect="plain" round class="status-tag">
                                <el-icon class="status-icon">
                                    <component :is="getStatusIcon(row.status)" />
                                </el-icon>
                                {{ getStatusText(row.status) }}
                            </el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column prop="createTime" label="挂号时间" width="170" sortable>
                        <template #default="{ row }">
                            <span class="text-gray text-small">{{ row.createTime }}</span>
                        </template>
                    </el-table-column>

                    <el-table-column label="操作" fixed="right" width="180" align="center">
                        <template #default="{ row }">
                            <template v-if="row.status === 0">
                                <el-button type="success" link size="small" icon="CircleCheck"
                                    @click="handleComplete(row)">
                                    完成
                                </el-button>
                                <el-button type="warning" link size="small" icon="CircleClose"
                                    @click="handleCancel(row)">
                                    取消
                                </el-button>
                            </template>
                            <el-button type="danger" link size="small" icon="Delete" @click="handleDelete(row)">
                                删除
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>

                <div class="pagination-wrapper">
                    <el-pagination v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize"
                        :page-sizes="[10, 20, 50, 100]" :total="total" layout="total, sizes, prev, pager, next, jumper"
                        background @size-change="handleSizeChange" @current-change="handleCurrentChange" />
                </div>
            </el-card>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { appointmentApi } from '@/api/appointment';
import type { AppointmentVO, AppointmentQueryDTO } from '@/types/appointment';
import {
    Search, Refresh, Back, Tickets, User, FirstAidKit,
    CircleCheck, CircleClose, Delete, Timer, Check, Close
} from '@element-plus/icons-vue';

const router = useRouter();

const tableData = ref<AppointmentVO[]>([]);
const loading = ref(false);
const total = ref(0);

const queryForm = reactive<AppointmentQueryDTO>({
    patientId: undefined,
    doctorId: undefined,
    status: undefined,
    pageNum: 1,
    pageSize: 10
});

// 状态工具函数
const getStatusType = (status: number) => {
    switch (status) {
        case 0: return 'primary';
        case 1: return 'success';
        case 2: return 'info';
        default: return 'info';
    }
};

const getStatusText = (status: number) => {
    switch (status) {
        case 0: return '待就诊';
        case 1: return '已完成';
        case 2: return '已取消';
        default: return '未知';
    }
};

const getStatusIcon = (status: number) => {
    switch (status) {
        case 0: return 'Timer';
        case 1: return 'Check';
        case 2: return 'Close';
        default: return 'Timer';
    }
};

const fetchData = async () => {
    loading.value = true;
    try {
        const res = await appointmentApi.getList(queryForm);
        if (res.code === 200) {
            tableData.value = res.data.records;
            total.value = res.data.total;
        }
    } catch (error) {
        console.error('获取挂号列表失败', error);
    } finally {
        loading.value = false;
    }
};

const handleSearch = () => {
    queryForm.pageNum = 1;
    fetchData();
};

const handleReset = () => {
    queryForm.patientId = undefined;
    queryForm.doctorId = undefined;
    queryForm.status = undefined;
    queryForm.pageNum = 1;
    handleSearch();
};

const handleSizeChange = (size: number) => {
    queryForm.pageSize = size;
    fetchData();
};

const handleCurrentChange = (page: number) => {
    queryForm.pageNum = page;
    fetchData();
};

const handleComplete = (row: AppointmentVO) => {
    ElMessageBox.confirm('确认该患者已完成就诊？', '就诊确认', {
        confirmButtonText: '确认完成',
        cancelButtonText: '取消',
        type: 'success'
    }).then(async () => {
        try {
            await appointmentApi.updateStatus(row.id, 1);
            ElMessage.success('操作成功');
            fetchData();
        } catch (error) {
            console.error('操作失败', error);
        }
    });
};

const handleCancel = (row: AppointmentVO) => {
    ElMessageBox.confirm('确定要取消此挂号吗？', '取消挂号', {
        confirmButtonText: '确认取消',
        cancelButtonText: '暂不取消',
        type: 'warning'
    }).then(async () => {
        try {
            await appointmentApi.updateStatus(row.id, 2);
            ElMessage.success('取消成功');
            fetchData();
        } catch (error) {
            console.error('取消失败', error);
        }
    });
};

const handleDelete = (row: AppointmentVO) => {
    ElMessageBox.confirm('确定删除此记录吗？删除后不可恢复。', '删除警告', {
        confirmButtonText: '永久删除',
        cancelButtonText: '取消',
        type: 'error'
    }).then(async () => {
        try {
            await appointmentApi.delete(row.id);
            ElMessage.success('删除成功');
            fetchData();
        } catch (error) {
            console.error('删除失败', error);
        }
    });
};

const goHome = () => {
    router.push('/home');
};

onMounted(() => {
    fetchData();
});
</script>

<style scoped lang="scss">
.page-container {
    padding: 20px;
    background-color: #f0f2f5;
    min-height: 100vh;
    font-family: 'Inter', sans-serif;
}

/* Page Header */
.page-header {
    background: #fff;
    padding: 20px 24px;
    margin: -20px -20px 20px -20px;
    border-bottom: 1px solid #e5e7eb;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
        .page-title {
            margin: 0 0 8px 0;
            font-size: 20px;
            font-weight: 600;
            color: #111827;
        }
    }
}

.main-content {
    max-width: 100%;
}

/* 搜索栏 */
.search-card {
    margin-bottom: 16px;
    border: none;
    border-radius: 8px;

    .search-form {
        :deep(.el-form-item) {
            margin-bottom: 0;
            margin-right: 24px;
        }
    }
}

/* 表格区域 */
.table-card {
    border: none;
    border-radius: 8px;

    .table-toolbar {
        margin-bottom: 16px;

        .left-tools {
            display: flex;
            align-items: center;
            gap: 8px;
            font-weight: 600;
            color: #374151;

            .text-gray {
                font-weight: 400;
                color: #9ca3af;
                font-size: 14px;
            }
        }
    }
}

/* 表格内容美化 */
.user-cell {
    display: flex;
    align-items: center;
    gap: 10px;

    .avatar {
        font-weight: 600;
        font-size: 14px;

        &.patient-bg {
            background: #e0e7ff;
            color: #4338ca;
        }

        &.doctor-bg {
            background: #dcfce7;
            color: #15803d;
        }
    }

    .info {
        .name {
            font-weight: 500;
            color: #1f2937;
            font-size: 14px;
        }

        .sub-text {
            font-size: 12px;
            color: #9ca3af;
        }
    }
}

.time-cell {
    display: flex;
    align-items: center;
    gap: 8px;
    font-family: 'Roboto Mono', monospace;
    color: #4b5563;
}

.status-tag {
    display: flex;
    align-items: center;
    gap: 4px;

    .status-icon {
        font-size: 14px;
    }
}

.mono-font {
    font-family: 'Roboto Mono', monospace;
    color: #6b7280;
}

.text-gray {
    color: #9ca3af;
}

.text-small {
    font-size: 12px;
}

.pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 24px;
}
</style>