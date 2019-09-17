import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { connect } from 'dva';
import { Card, Row, Col, message, Button } from 'antd';
import { IMAGE } from '@/assets/index';
import { service } from '@/services/service';
import GitLab from './gitLabItem';
import Git from './gitItem';
import styles from '@/pages/index.less';
import router from 'umi/router'
const CodeBegin = [
  {
    type: 'GitLab',
    imgPath: IMAGE.GITLAB,
    text: 'OPS托管Gitlab'
  },
  {
    type: 'Git',
    imgPath: IMAGE.GIT,
    text: 'git代码仓库'
  },
  {
    type: 'GitHub',
    imgPath: IMAGE.GITHUB,
    text: 'GitHub代码仓库',
    isDev: true
  },
  {
    type: 'SVN',
    imgPath: IMAGE.SVN,
    text: 'SVN',
    isDev: true
  }
];
const DockerBegin = [
  {
    type: 'Docker',
    imgPath: IMAGE.DOCKER,
    text: '外部镜像库',
    isDev: true
  },
];
const AppBegin = [
  {
    type: 'Redies',
    imgPath: IMAGE.REDIES,
    text: 'Redies',
    version: '5.0.5',
    desc: '官方',
    isDev: true
  },
  {
    type: 'Zookeeper',
    imgPath: IMAGE.ZOOKEEPER,
    text: 'zookeeper',
    version: '3.5.5',
    desc: '社区',
    isDev: true
  },
  {
    type: 'Mysql',
    imgPath: IMAGE.MYSQL,
    text: 'Mysql-Server',
    version: '5.2.27',
    desc: '官方',
    isDev: true
  },
  {
    type: 'springcloud',
    imgPath: IMAGE.SPRINGCLOUD,
    text: 'Spring cloud eureka',
    version: '2.0',
    desc: '官方',
    isDev: true
  },
  {
    type: 'Pinpoint',
    imgPath: IMAGE.PINPOINT,
    text: 'pinpoint集群',
    version: '1.8.4',
    desc: '官方',
    isDev: true,
    isAtaGitlab: false
  },
];
class AddService extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showType: '',
      projectId: undefined
    };
  }
  componentDidMount() {
    const { location } = this.props;
    let query = location.query || {};
    this.setState({
      projectId: query.projectId
    })
    this.onFetchIsAtaGitlab();
  }
  onFetchIsAtaGitlab = async () => {
    let res = (await service.isAtaGitlab()) || {};
    this.setState({
      isAtaGitlab: res.data ? true : false
    })
  }
  onAdd = (item) => {
    if (item.isDev) {
      message.info('规划中...');
      return
    }
    this.setState({
      showType: item.type
    })
  }
  //上一步
  onCancel = () => {
    this.setState({
      showType: ''
    })
  }
  onSave = (params) => {
    this.onFetchAppCreate(params)
  }
  onFetchAppCreate = async (params) => {
    let res = await service.appCreate(params);
    if (res && res.code == '1') {
      message.success('添加成功');
      this.setState({
        showType: ''
      })
      router.goBack();
    }
  }
  renderCodeBegin() {
    const { isAtaGitlab } = this.state;
    return (
      <Card title="从代码开始">
        <Row type="flex" gutter={36} align="middle">
          {
            CodeBegin.map((item) => (
              <Col className={styles.textC} key={item.type} style={{ width: '20%' }}>
                {
                  item.isDev ?
                    <div>
                      <img src={item.imgPath} title={"规划中"}></img>
                      <p className={styles.disabledColor}>{item.text}</p>
                    </div> :
                    item.type == 'GitLab' && !isAtaGitlab ?
                      <div>
                        <img src={IMAGE.GITLABDISABLED} title="未安装"></img>
                        <p className={styles.disabledColor}>{item.text}</p>
                      </div> :
                      <div onClick={() => { this.onAdd(item) }}>
                        <img src={item.imgPath} title={item.text}></img>
                        <p>{item.text}</p>
                      </div>
                }

              </Col>
            ))
          }
        </Row>
      </Card>
    )
  }
  renderDockerBegin() {
    return (
      <Card title="从镜像开始" className={styles.marginT}>
        <Row type="flex" gutter={36} align="middle">
          {
            DockerBegin.map((item) => (
              <Col className={styles.textC} key={item.type} style={{ width: '20%' }} onClick={() => { this.onAdd(item) }}>
                <img src={item.imgPath} title={item.isDev ? '规划中' : item.text}></img>
                <p className={item.isDev ? styles.disabledColor : ''}>{item.text}</p>
              </Col>
            ))
          }
        </Row>
      </Card>
    )
  }
  renderAppBegin() {
    return (
      <Card title="从应用市场开始" className={styles.marginT}>
        <Row type="flex" align="middle" gutter={36}>
          {
            AppBegin.map((item) => (
              <Col className={styles.textC} key={item.type} style={{ width: '20%' }}>
                <Card onClick={() => { this.onAdd(item) }}>
                  <div style={{ height: 64, lineHeight: '64px' }}>
                    <img src={item.imgPath} title={item.isDev ? '规划中' : item.text}></img>
                  </div>
                  <p className={item.isDev ? styles.disabledColor : ''}>{item.text}</p>
                  <p className={item.isDev ? styles.disabledColor : ''}>最高版本：{item.version}</p>
                  <Button disabled size="small">{item.desc}</Button>
                </Card>
              </Col>
            ))
          }
        </Row>
      </Card>
    )
  }
  renderAddItem() {
    const { showType, projectId } = this.state;
    return (
      <Fragment>
        {
          showType === 'GitLab' && <GitLab onCancel={this.onCancel} onSave={this.onSave} projectId={projectId} />
        }
        {
          showType === 'Git' && <Git onCancel={this.onCancel} onSave={this.onSave} projectId={projectId} />
        }
      </Fragment>
    )
  }
  render() {
    const { showType } = this.state;
    return (
      <PageHeaderWrapper title="新增服务" content="可以根据服务的类型不同，从代码、镜像或者应用市场创建自己的服务">
        {!showType &&
          <Fragment>
            {this.renderCodeBegin()}
            {this.renderDockerBegin()}
            {this.renderAppBegin()}
          </Fragment>
        }

        {this.renderAddItem()}
      </PageHeaderWrapper>
    );
  }
}

export default connect(({ service }) => ({

}))(AddService);
