import React, { Fragment } from 'react';
import { Card, Drawer, Table, Popconfirm, Divider, Form, message, Icon } from 'antd';
import { connect } from 'dva';
import { getCurrentTime, NoGitUrl } from '@/utils/utils';
import { service } from '@/services/service';
import stylesService from '../index.less';
import ImagePushModal from './imagePushModal';
import styles from '@/pages/index.less'


class Mirror extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      currentItem: {},
      showPushModal: false,
      pushLoading: false,
      deployType: '',
      logDetail: [],
      deleteLoading: false
    };
  }
  componentDidMount() {
    const { appRunCurrentImage } = this.props;
    this.onFetchList();
    if (Object.getOwnPropertyNames(appRunCurrentImage).length === 0) {
      this.onFetchAppRunCurrentImage();
    }
  }

  componentWillReceiveProps(nextProps) {
    const { appDeployStatus, appOperate } = nextProps;
    const { deployType, pushLoading } = this.state;
    // if (this.props.appDeployStatus !== appDeployStatus && !appDeployStatus.deploying) {
    //   if (pushLoading) {
    //     this.setState({
    //       pushLoading: false
    //     })
    //   }
    //   if(appDeployStatus.deployType == 'DELETE_IMAGE' || appDeployStatus.deployType == 'PUSH_IMAGE'){
    //     this.onFetchList()
    //   }
    //   if (this.props.appDeployStatus.deployType) {
    //     if (appDeployStatus.deployType == 'DELETE_IMAGE') {
    //       message.success('删除成功');
    //       return
    //     }
    //     if (appDeployStatus.deployType == 'PUSH_IMAGE') {
    //       message.success('推送成功');
    //       return
    //     }
    //   }
    // }
    // if ((!appOperate.event && appOperate.event !== this.props.appOperate.event) || (!appOperate.event)) {
    //   if (pushLoading) {
    //     this.setState({
    //       pushLoading: false
    //     })
    //   }
    // }
  }
  componentWillUnmount() {
    clearInterval(this.timmer)
  }
  onFetchList = (params = {}) => {
    const { data, dispatch } = this.props;
    dispatch({
      type: 'service/dockerList',
      payload: {
        appId: data.appId,
        envId: data.envId,
        page: params.page || 1,
        size: params.size || 10,
        ...params
      }
    })
  }
  onFetchAppRunCurrentImage = () => {
    const { data, dispatch } = this.props;
    dispatch({
      type: 'service/appRunCurrentImage',
      payload: {
        appId: data.appId,
        envId: data.envId
      }
    })
  }
  onShowPushModal = (record) => {
    this.setState({
      currentItem: record,
      showPushModal: true
    })
  }
  onDelete = (record) => {
    this.setState({
      deployType: 'DELETE_IMAGE',
      currentItem: record,
      deleteLoading: true
    })
    this.onAppOperate('DELETE_IMAGE', { dockerImageTag: record.imageTag, imageId: record.id })
  }
  //分页,筛选,排序
  onTableChange = (pagination, filters, sorter) => {
    const { current, pageSize } = pagination;
    const { order, field } = sorter;
    let sort = order ? {
      sortValue: order === 'ascend' ? 'asc' : order === 'descend' ? 'desc' : '',
      sortName: field ? field : ''
    } : {};
    let params = {
      ...sort,
      page: current,
      size: pageSize,
    };
    this.onFetchList(params);
  };
  onAppOperate = async (event, detail = {}) => {
    const { data } = this.props;
    let parmas = { appId: data.appId, envId: data.envId, detail: JSON.stringify({ ...detail, appId: data.appId, envId: data.envId }), event };
    let res = await service.appOperate(parmas);
    if (res && res.code == '1') {
      this.timmer = setInterval(() => {
        this.onFetchStatus(res.data.eventId);
        this.onGetLogDetail(res.data.eventId);
      }, 3000)
    } else {
      this.setState({
        pushLoading: false
      })
    }
  }
  onFetchStatus = async (eventId) => {
    const { deployType } = this.state;
    let res = await service.appOperateStatus({ eventId });
    if (res && res.code == '1') {
      this.onFetchList()
      if (res.data.status == 'SUCCESS') {
        clearInterval(this.timmer);
        this.setState({
          pushLoading: false,
          deleteLoading: false,
          currentItem: {}
        })
        if (deployType == "DELETE_IMAGE") {
          message.success('删除成功');
        }
        if (deployType == "PUSH_IMAGE") {
          message.success('推送成功');
        }
        this.onGetLogDetail(eventId)
        return
      }
      if (res.data.status == 'FAILURE') {
        clearInterval(this.timmer);
        this.setState({
          pushLoading: false
        })
        if (deployType == "DELETE_IMAGE") {
          message.error('删除失败');
        }
        if (deployType == "PUSH_IMAGE") {
          message.error('推送失败');
        }

        this.onGetLogDetail(eventId)
        return
      }
    } else {
      clearInterval(this.timmer);
    }
  }
  //查看详情
  onGetLogDetail = async (eventId) => {
    let res = await service.appOperateLog({ eventId });
    if (res && res.code == '1') {
      let data = res.data || [];
      this.setState({
        logDetail: data
      })
    }
  }
  // onAppOperate= (event, detail = {}) => {
  //   const { data, dispatch } = this.props;
  //   let parmas = { appId: data.appId, envId: data.envId, detail: JSON.stringify({ ...detail, appId: data.appId, envId: data.envId }), event };
  //       dispatch({
  //     type: 'service/appOperate',
  //     payload: parmas
  //   })
  // }
  onCancel = () => {
    this.setState({
      showPushModal: false,
      logDetail: [],
      currentItem: {}
    })
  }
  onToGit = (gitId) => {
    const { appRunStatus } = this.props;
    let baseUrl = `${NoGitUrl(appRunStatus.gitRepository)}/commit/`;
    window.open(baseUrl + gitId)
  }
  onSubmit = (values) => {
    const { currentItem } = this.state;
    this.setState({
      pushLoading: true
    })
    this.setState({
      deployType: 'PUSH_IMAGE'
    })
    this.onAppOperate('PUSH_IMAGE', { ...values, dockerImageTag: currentItem.imageTag, imageId: currentItem.id })
  };
  renderPushModal() {
    const { showPushModal, pushLoading, currentItem, logDetail } = this.state;
    return (
      <ImagePushModal visible={showPushModal} confirmLoading={pushLoading} currentItem={currentItem} onCancel={this.onCancel} onSubmit={this.onSubmit} logDetail={logDetail} />
    )
  }
  render() {
    const { visible, onClose, list, loading, appRunCurrentImage } = this.props;
    const { showPushModal, deleteLoading, currentItem } = this.state;
    const columns = [
      {
        title: '镜像Tag',
        dataIndex: 'imageTag',
      },
      {
        title: '关联git提交记录',
        dataIndex: 'gitCommitId',
        render: (text, record) => (
          <Fragment>
            {
              text &&
              <a onClick={() => this.onToGit(text)}>
                <span>{text.slice(0, 8)}</span>
                <span>/{getCurrentTime(record.gitCommitTime)}</span>
              </a>
            }
          </Fragment>
        )
      },
      {
        title: '操作',
        dataIndex: 'opera',
        render: (text, record) => (
          <div>
            {
              deleteLoading ?
                currentItem.id === record.id ?
                  <span className={styles.disabledColor}>
                    <Icon type="loading" />
                    <span>删除</span>
                  </span> :
                  <span className={styles.disabledColor}>删除</span> :
                <Popconfirm
                  title="确定删除吗?"
                  onConfirm={() => this.onDelete(record)}
                  okText="确定"
                  cancelText="取消"
                >
                  <a>删除</a>
                </Popconfirm>
            }

            <Divider type="vertical" />
            <a
              onClick={() => {
                this.onShowPushModal(record);
              }}
            >
              推送到
              </a>

          </div>
        )
      },
    ];
    return (
      <Fragment>
        <Drawer
          title={
            <div>
              <span>镜像管理</span>
              {
                appRunCurrentImage.registry &&
                <span className={stylesService.subTitle}>镜像名：{appRunCurrentImage.registry}/{appRunCurrentImage.imageName}</span>
              }
            </div>
          }
          placement="right"
          closable={false}
          onClose={onClose}
          visible={visible}
          width={800}
        >
          <Table
            // rowSelection={rowSelection}
            columns={columns}
            dataSource={list.rows || []}
            onChange={this.onTableChange}
            loading={loading}
            pagination={{
              // showQuickJumper: true,
              // pageSizeOptions: ['10', '20', '30', '50'],
              total: list.totalCount || 0,
              showTotal: (total, range) =>
                `共${list.totalCount || 0}条，当前${list.pageNum ? list.pageNum : 1}/${
                list.totalPage ? list.totalPage : 1
                }页`,
              // showSizeChanger: true,
              current: list.pageNum ? list.pageNum : 1,
              pageSize: list.pageSize ? list.pageSize : 10,
            }}
            rowKey={record => {
              return record.imageTag;
            }}
          />
        </Drawer>
        {showPushModal && this.renderPushModal()}
      </Fragment>
    );
  }
}

export default Form.create()(connect(({ service, loading }) => ({
  list: service.dockerList,
  appRunCurrentImage: service.appRunCurrentImage,
  appRunStatus: service.appRunStatus,
  appDeployStatus: service.appDeployStatus,
  appOperate: service.appOperate,
  loading: loading.effects["service/dockerList"]
}))(Mirror));