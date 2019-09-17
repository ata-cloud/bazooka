import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Row, Col, Descriptions, Icon, Table, Badge, Modal, Button, Typography } from 'antd';
import styles from '../index.less'
import {
  G2,
  Chart,
  Geom,
  Axis,
  Tooltip,
  Coord,
  Label,
  Legend,
  View,
  Guide,
  Shape,
  Facet,
  Util
} from "bizcharts";
import DataSet from "@antv/data-set";
import { cluster } from '@/services/cluster';
import { CLUSTER_TYPE_O, CLUSTER_STATUS } from '@/common/constant';
import Link from 'umi/link';
import { MformG } from '@/utils/utils';
const { DataView } = DataSet;
const { Html } = Guide;
// const dv = new DataView();
const { Paragraph, Text } = Typography;
class ClusterDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      clusterId: '',
      detail: {},
      nodeList: {},
      nodeListLoading: false,
      showAddNode: false
    };
  }
  componentDidMount() {
    const { location } = this.props;
    let query = location.query || {};
    if (query.clusterId) {
      this.setState({
        clusterId: query.clusterId
      }, () => {
        this.onFetchDetail();
        this.onFetchNodeData();
      })
    }
  }
  // --------------------方法 --------------------------------------------------
  onFetchDetail = async () => {
    const { clusterId } = this.state;
    let res = await cluster.getClusterDetail({ clusterId });
    if (res && res.code == '1') {
      this.setState({
        detail: res.data || {}
      })
    }
  }
  onFetchNodeData = async (params = {}) => {
    const { clusterId } = this.state;
    this.setState({
      nodeListLoading: true
    })
    let res = await cluster.getClusterNodePage({ ...params, clusterId });
    this.setState({
      nodeList: res.data || {},
      nodeListLoading: false
    })
  }
  onDownOff = (item) => {
    console.log('onDownOff-->下线')
  }
  onNodeReload = () => {
    this.onFetchNodeData();
  }
  onNodeTableChange = (pagination, filters, sorter) => {
    const { current, pageSize } = pagination;
    this.onFetchNodeData({ pageNo: current, pageSize });
  }
  showAddNode = () => {
    this.setState({
      showAddNode: true
    })
  }
  onAddModalCancel = () => {
    this.setState({
      showAddNode: false
    })
  }
  onHref = (url) => {
    window.open(`http://${url}`)
  }
  //------------------------页面渲染-------------------------------------
  renderChart(data, dv, field, text, type, unit) {
    const { detail } = this.state;
    dv.source(data).transform({
      type: "percent",
      field: field,
      dimension: "name",
      as: "percent"
    });

    const cols = {
      percent: {
        formatter: val => {
          val = (val * 100).toFixed(2) + "%";
          return val;
        }
      },
      id: {
        formatter: val => {
          return val || '';
        }
      },
    };
    return (
      <div>
        <Chart
          height={300}
          data={dv}
          scale={cols}
          padding={[50, 150, 50, 50]}
          forceFit
        >
          <Coord type={"theta"} radius={1} innerRadius={0.6} />
          <Axis name="percent" />
          <Tooltip
            showTitle={false}
            itemTpl="<li><span style=&quot;background-color:{color};&quot; class=&quot;g2-tooltip-marker&quot;></span>{name}: {value}</li>"
          />
          <Guide>
            <Html
              position={["50%", "50%"]}
              html={`<div style="color:#333;font-size:20px;text-align: center;width: 10em">${text}<br><span style="color:#262626;font-size:14px">${type !== 'cpu' ? MformG(detail[type]) : detail[type]} ${unit}</span></div>`}
              alignX="middle"
              alignY="middle"
            />
          </Guide>
          <Geom
            type="intervalStack"
            position="percent"
            color={['id', (type) => {
              if (!type)
                return '#999999';
            }]}
            tooltip={[
              "name*percent",
              (item, percent) => {
                percent = (percent * 100).toFixed(2) + "%";
                return {
                  name: item ? item : '未使用',
                  value: percent
                };
              }
            ]}
            style={{
              lineWidth: 1,
              stroke: "#fff"
            }}
          >
            <Label
              content="percent"
              formatter={(val, item) => {
                let name = item.point.name ? item.point.name : '未使用';
                return name + ": " + val;
              }}
            />
          </Geom>
        </Chart>
      </div>
    );
  }
  renderBasic() {
    const { detail } = this.state;
    return (
      <Card title="集群基础信息" className={styles.marginB}>
        {/* <Descriptions>
          <Descriptions.Item label="命名">{detail.name}</Descriptions.Item>
          <Descriptions.Item label="类型">{detail.type && CLUSTER_TYPE_O[detail.type] ? CLUSTER_TYPE_O[detail.type] : ''}</Descriptions.Item>
          <Descriptions.Item label="集群master">
            {
              detail.marathons && detail.marathons.map((item, index) => (
                <Fragment key={index}>
                  {
                    item.status && CLUSTER_STATUS[item.status] &&
                    <Badge status={CLUSTER_STATUS[item.status].type} />
                  }
                  <a onClick={() => this.onHref(item.url)}>{item.url}</a>
                  {
                    index !== detail.marathons.length - 1 && <span> | </span>
                  }
                </Fragment>
              ))
            }
          </Descriptions.Item>
          <Descriptions.Item label="状态">
            {
              detail.status && CLUSTER_STATUS[detail.status] &&
              <Fragment>
                <Icon type={CLUSTER_STATUS[detail.status].icon} style={{ fontSize: 25, color: CLUSTER_STATUS[detail.status].colorValue }} />
              </Fragment>
            }
          </Descriptions.Item>
          <Descriptions.Item label="DC/OS版本">{detail.version}</Descriptions.Item>
          <Descriptions.Item label="集群LB详情">
            {
              detail.mlbs && detail.mlbs.map((item, index) => (
                <div key={index}>
                  {
                    item.status && CLUSTER_STATUS[item.status] &&
                    <Badge status={CLUSTER_STATUS[item.status].type} />
                  }
                  <a onClick={() => this.onHref(item.url + ':9090/haproxy?stats')}>{item.url + ':9090/haproxy?stats'}</a>
                </div>
              ))
            }
          </Descriptions.Item>
        </Descriptions>
        */}
        {/* <Row>
          <Col span={8}></Col>
          <Col span={8}></Col>
          <Col span={8}>
            <div className={styles.flexCenter}>
              <div style={{ color: '#333' }}>镜像库：</div>
              <div>
                {
                  detail.dockerHubs && detail.dockerHubs.map((item, index) => (
                    <div key={index}>
                      {
                        item.status && CLUSTER_STATUS[item.status] &&
                        <Badge status={CLUSTER_STATUS[item.status].type} />
                      }
                      <a onClick={() => this.onHref(item.url)}>{item.url}</a>
                    </div>
                  ))
                }
              </div>
            </div>
          </Col>
        </Row> */}
        <Row type="flex" align="top">
          <Col md={6}>
            <div className={styles.flex}>
              <p className={styles.textBlack}>命名：</p>
              <p>{detail.name}</p>
            </div>
          </Col>
          <Col md={6}>
            <div className={styles.flex}>
              <p className={styles.textBlack}>类型：</p>
              <p>{detail.type && CLUSTER_TYPE_O[detail.type] ? CLUSTER_TYPE_O[detail.type] : ''}</p>
            </div>
          </Col>
          <Col md={12}>
            <div className={styles.flex}>
              <p className={styles.textBlack}>集群master：</p>
              <p>
                {
                  detail.marathons && detail.marathons.map((item, index) => (
                    <Fragment key={index}>
                      {
                        item.status && CLUSTER_STATUS[item.status] &&
                        <Badge status={CLUSTER_STATUS[item.status].type} />
                      }
                      <a onClick={() => this.onHref(item.url)}>{item.url}</a>
                      {
                        index !== detail.marathons.length - 1 && <span> | </span>
                      }
                    </Fragment>
                  ))
                }
              </p>
            </div>
          </Col>
          <Col md={6}>
            <div className={styles.flex}>
              <p className={styles.textBlack}>状态：</p>
              <p>
                {
                  detail.status && CLUSTER_STATUS[detail.status] &&
                  <Fragment>
                    <Icon type={CLUSTER_STATUS[detail.status].icon} style={{ fontSize: 25, color: CLUSTER_STATUS[detail.status].colorValue }} />
                  </Fragment>
                }
              </p>
            </div>
          </Col>
          <Col md={6}>
            <div className={styles.flex}>
              <p className={styles.textBlack}>DC/OS版本：</p>
              <p>{detail.version}</p>
            </div>
          </Col>
          <Col md={12}>
            <div className={styles.flex}>
              <p className={styles.textBlack}>集群LB详情：</p>
              <div>
                {
                  detail.mlbs && detail.mlbs.map((item, index) => (
                    <p key={index}>
                      {
                        item.status && CLUSTER_STATUS[item.status] &&
                        <Badge status={CLUSTER_STATUS[item.status].type} />
                      }
                      <a onClick={() => this.onHref(item.url + ':9090/haproxy?stats')}>{item.url + ':9090/haproxy?stats'}</a>
                    </p>
                  ))
                }
              </div>
            </div>
          </Col>
          <Col md={12} push={12}>
            <div className={styles.flex}>
              <p className={styles.textBlack}>镜像库：</p>
              <div>
                {
                  detail.dockerHubs && detail.dockerHubs.map((item, index) => (
                    <p key={index}>
                      {
                        item.status && CLUSTER_STATUS[item.status] &&
                        <Badge status={CLUSTER_STATUS[item.status].type} />
                      }
                      <a onClick={() => this.onHref(item.url)}>{item.url}</a>
                    </p>
                  ))
                }
              </div>
            </div>
          </Col>
        </Row>
      </Card>
    )
  }
  renderUseInfo() {
    const { detail } = this.state;
    const dvCpu = new DataView();
    const dvMemory = new DataView();
    const dvDist = new DataView();
    let envResources = detail.envResources || [];
    return (
      <Card title="使用情况" className={styles.marginB}>
        <Row>
          <Col md={8} sm={24}>
            {this.renderChart(envResources, dvCpu, 'cpus', 'CPU', 'cpu', 'Core')}
          </Col>
          <Col md={8} sm={24}>
            {this.renderChart(envResources, dvMemory, 'memory', '内存', 'memory', 'GiB')}
          </Col>
          <Col md={8} sm={24}>
            {this.renderChart(envResources, dvDist, 'disk', '磁盘', 'disk', 'GiB')}
          </Col>
        </Row>
      </Card>
    )
  }
  renderAddNodeModal() {
    const { showAddNode } = this.state;
    return (
      <Modal
        visible={showAddNode}
        title="新增节点"
        footer={null}
        onCancel={this.onAddModalCancel}
      >
        <p>请在需要加入DC/OS集群的节点上<span>安装Docker</span>之后，运行以下新增节点命令</p>
        <Row type="flex" justify="space-between" align="middle" className={styles.copyBg}>
          <Col span={20}>
            <Text className={styles.colorWhite}>curl -sSL https://ata.cloud.local.com/shells/install.sh | sh -s cd4f4bdd8d7d7cde0e</Text>
          </Col>
          <Col>
            <Text copyable={{ text: 'curl -sSL https://ata.cloud.local.com/shells/install.sh | sh -s cd4f4bdd8d7d7cde0e' }}></Text>
          </Col>
        </Row>
      </Modal>
    )
  }
  renderNode() {
    const { nodeList, nodeListLoading, showAddNode } = this.state;
    const columns = [
      {
        title: 'IP',
        dataIndex: 'ip'
      },
      {
        title: '节点类型',
        dataIndex: 'nodeType'
      },
      {
        title: '健康状态',
        dataIndex: 'status',
        render: (text, record) => (
          <span>
            <Badge status={text === '0' ? 'success' : text === "-1" ? 'default' : 'error'} />
          </span>
        )
      },
      {
        title: '容器',
        dataIndex: 'containerQuantity'
      },
      {
        title: 'CPU',
        dataIndex: 'cpu',
        render: (text, record) => (
          <span>{`${record.usedCpu}/${text} (${Math.round(record.usedCpu / text * 100)}%)`}</span>
        )
      },
      {
        title: '内存',
        dataIndex: 'memory',
        render: (text, record) => (
          <span>{`${MformG(record.usedMemory)}/${MformG(text)} GiB (${Math.round(record.usedMemory / text * 100)}%)`}</span>
        )
      },
      {
        title: '磁盘',
        dataIndex: 'disk',
        render: (text, record) => (
          <span>{`${MformG(record.usedDisk)}/${MformG(text)} GiB (${Math.round(record.usedDisk / text * 100)}%)`}</span>
        )
      },
      // {
      //   title: '操作',
      //   dataIndex: 'opera',
      //   render: (text, record) => (
      //     <div>
      //       <a
      //         onClick={() => {
      //           this.onDownOff(record);
      //         }}
      //       >
      //         下线
      //       </a>
      //     </div>
      //   )
      // }
    ];
    return (
      <Card title={
        <div>
          <span className={styles.marginR}>节点</span>
          <Icon type="reload" onClick={this.onNodeReload} />
        </div>
      }
      //  extra={
      //   // <Button type="primary" onClick={this.showAddNode}>+ 新增节点</Button>
      // }
      >
        <Table
          columns={columns}
          dataSource={nodeList.rows || []}
          onChange={this.onNodeTableChange}
          loading={nodeListLoading}
          pagination={{
            // pageSizeOptions: ['10', '20', '30', '50'],
            total: nodeList.totalCount || 0,
            showTotal: (total, range) =>
              `共${nodeList.totalCount || 0}条，当前${nodeList.pageNum ? nodeList.pageNum : 1}/${
              nodeList.totalPage ? nodeList.totalPage : 1
              }页`,
            // showSizeChanger: true,
            current: nodeList.pageNum ? nodeList.pageNum : 1,
            pageSize: nodeList.pageSize ? nodeList.pageSize : 10,
          }}
          rowKey={record => {
            return record.id;
          }}
        />
        {/* {
          showAddNode && this.renderAddNodeModal()
        } */}
      </Card>
    )
  }
  render() {
    const { detail } = this.state;
    return (
      <PageHeaderWrapper title={detail.name}>
        {this.renderBasic()}
        {this.renderUseInfo()}
        {this.renderNode()}
      </PageHeaderWrapper>
    );
  }
}
export default ClusterDetail;
