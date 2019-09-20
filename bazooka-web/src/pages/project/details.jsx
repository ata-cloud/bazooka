import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Tabs, Input, Button, Row, Col } from 'antd';
import styles from "@/pages/index.less";
import { connect } from 'dva';
const { TabPane } = Tabs;
import { ProjectInfo, ProjectConfig, ProjectList } from './detail';
import router from 'umi/router';
class ProjectDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      key: '1',
      projectId: undefined,
      tabList: [
        {
          key: '1',
          tab: '服务列表'
        },
        {
          key: '2',
          tab: '项目信息'
        },
        {
          key: '3',
          tab: '项目配置'
        },
      ],
      keys: [],
      keyword: ""
    };
  }
  componentDidMount() {
    const { location } = this.props;
    let query = location.query || {};
    this.setState({
      projectId: query.projectId
    })
    this.onFetchInfo(query.projectId);
  }
  componentWillUnmount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'project/clearData',
      payload: {
        proInfo: {},
        proEnvSoruce: [],
        devUserList: {},
        deployCounts: []
      }
    })
    dispatch({
      type: 'service/clearData',
      payload: {
        appList: []
      }
    })
  }  //项目信息
  onFetchInfo = (projectId) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'project/proInfo',
      payload: { projectId }
    })
  }

  onTabChange = (key) => {
    const { keys } = this.state;
    this.setState({
      key
    })
  }
  onSearch = (value) => {
    this.setState({
      keyword: value
    })
  }
  onAddServixe = () => {
    const { proInfo } = this.props;
    router.push({
      pathname: '/service/add',
      query: {
        projectId: proInfo.id
      }
    })
  }
  render() {
    const { key, projectId, tabList, keys, keyword } = this.state;
    const { proInfo } = this.props;
    return (
      <Fragment>
        {
          proInfo.id &&
          <PageHeaderWrapper title={proInfo.projectName} content={
            <Fragment>
              {
                key == "1" ?
                  <Row type="flex" justify="end" align="middle">
                    <Col>
                      <Input.Search placeholder="搜索服务" onSearch={this.onSearch} />
                    </Col>
                    <Col>
                      <Button className={styles.marginL} type="primary" onClick={this.onAddServixe}>+ 新增服务</Button>
                    </Col>
                  </Row> :
                  <Row type="flex" justify="end" align="middle">
                    <Col>
                      <Input style={{ visibility: 'hidden' }} />
                    </Col>
                  </Row>
              }
            </Fragment>
          } tabList={tabList} onTabChange={this.onTabChange} tabActiveKey={key}>
            {
              key === '1' &&
              <ProjectList projectId={projectId} keyword={keyword} />
            }
            {
              key === '2' &&
              <ProjectInfo projectId={projectId} />
            }
            {
              key === '3' &&
              <ProjectConfig projectId={projectId} />
            }
          </PageHeaderWrapper>
        }
      </Fragment>

    );
  }
}
export default connect(({ project, service }) => ({
  proInfo: project.proInfo,
}))(ProjectDetail);
