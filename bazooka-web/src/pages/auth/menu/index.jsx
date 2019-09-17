import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Form, Input, Row, Col, Button, Table, Divider, Popconfirm, message, Popover } from 'antd';
import { connect } from 'dva';
import { auth } from '@/services/auth';
import moment from 'moment';
import styles from '../../index.less';
const FormItem = Form.Item;
let obj = {};
class Menu extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      searchObj: {},
    };
  }
  // --------------------生命周期 --------------------------------------------------
  componentDidMount() {
    const { list } = this.props;
    this.setState({
      searchObj: obj
    }, () => {
      if (Object.getOwnPropertyNames(list).length === 0) {
        this.onFetchList();
      }
    })
  }
  componentWillUnmount() {
    const { searchObj } = this.state;
    obj = searchObj;
  }
  // --------------------方法 --------------------------------------------------
  //列表
  onFetchList = (payload) => {
    const { searchObj } = this.state;
    const { dispatch } = this.props;
    let params = payload ? payload : searchObj;
    dispatch({
      type: 'auth/permissionList',
      payload: params
    })
  }
  //查询
  onSubmit = (e) => {
    const { searchObj } = this.state;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        let params = { ...searchObj, ...values };
        this.setState({
          searchObj: params
        })
        this.onFetchList({ ...params, pageNo: 1 })
      }
    });
  };
  //分页,筛选,排序
  onTableChange = (pagination, filters, sorter) => {
    const { current, pageSize } = pagination;
    const { order, field } = sorter;
    const { searchObj } = this.state;
    let sort = order ? {
      sortValue: order === 'ascend' ? 'asc' : order === 'descend' ? 'desc' : '',
      sortName: field ? field : ''
    } : {};
    let params = {
      ...searchObj,
      ...sort,
      pageNum: pageSize === searchObj.pageSize ? parseInt(current) : 1,
      pageSize: parseInt(pageSize),
    };
    this.setState({
      searchObj: params
    }, () => {
      this.onFetchList({ ...params });
    });
  };
  //重置
  onReset = () => {
    const { searchObj } = this.state;
    const { resetFields } = this.props.form;
    resetFields();
    this.setState({
      searchObj: {}
    })
  }
  //编辑or添加
  onEdit = (record) => {
    let query = record ? { id: record.permissionId } : {};
    this.props.history.push({
      pathname: '/auth/menu/edit',
      query
    });
  };
  //删除
  onDelete = (permissionId) => {
    const { searchObj } = this.state;
    auth.permissionDelete({ permissionId })
      .then((res) => {
        if (res && res.code == '1') {
          message.success('删除成功');
          this.onFetchList({
            ...searchObj,
            pageNo: 1
          });
        }
      })
  }
  // --------------------渲染 --------------------------------------------------
  renderSearch() {
    const { searchObj } = this.state;
    const { getFieldDecorator } = this.props.form;
    return (
      <Fragment>
        <Form className={styles.antAdvancedSearchForm} onSubmit={this.onSubmit}>
          <Row>
            <Col span={8}>
              <FormItem label="权限名称">
                {getFieldDecorator('permissionName', {
                  initialValue: searchObj.permissionName,
                })(<Input placeholder="请输入权限名称" className={styles.inputWidth} />)}
              </FormItem>
            </Col>
            <Col span={6}>
              <FormItem label=" " colon={false}>
                <Button type="primary" htmlType="submit" style={{ marginRight: 10 }}>
                  查询
                </Button>
                <Button onClick={this.onReset}>重置</Button>
              </FormItem>
            </Col>
          </Row>
        </Form>
      </Fragment>
    )
  }
  renderOpera() {
    return (
      <Fragment>
        <Button type="primary" onClick={() => this.onEdit()}>
          添加权限
        </Button>
      </Fragment>
    )
  }
  renderTable() {
    const { list, loading } = this.props;
    const columns = [
      {
        title: '权限名称',
        dataIndex: 'permissionName',
      },
      {
        title: '地址',
        dataIndex: 'url',
      },
      {
        title: '备注',
        dataIndex: 'remark',
        className: styles.textOver,
        render: (text, record) => (
          <Fragment>
            {
              text ?
                <Popover content={
                  <span>{text}</span>
                } title={null} trigger="hover">
                  <a>{text}</a>
                </Popover> : ''
            }
          </Fragment>

        )
      },
      {
        title: '创建人',
        dataIndex: 'createAuthor',
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
        render: (text, record) => (
          <span>{text ? moment(text).format('YYYY-MM-DD HH:mm:ss') : ''}</span>
        )
      },
      {
        title: '操作',
        dataIndex: 'opera',
        render: (text, record) => (
          <div>
            <span>
              <a
                onClick={() => {
                  this.onEdit(record);
                }}
              >
                编辑
              </a>
              <Divider type="vertical" />
              <Popconfirm
                title="确定删除吗?"
                onConfirm={() => this.onDelete(record.permissionId)}
                okText="确定"
                cancelText="取消"
              >
                <a>删除</a>
              </Popconfirm>
            </span>
          </div>
        ),
      },
    ];
    return (
      <Fragment>
        <div style={{ margin: '20px 0' }}>
          <Table
            // rowSelection={rowSelection}
            columns={columns}
            dataSource={list.rows || []}
            onChange={this.onTableChange}
            loading={loading}
            pagination={{
              // showQuickJumper: true,
              pageSizeOptions: ['10', '20', '30', '50'],
              total: list.totalCount || 0,
              showTotal: (total, range) =>
                `共${list.totalCount || 0}条，当前${list.pageNum ? list.pageNum : 1}/${
                list.totalPage ? list.totalPage : 1
                }页`,
              showSizeChanger: true,
              current: list.pageNum ? list.pageNum : 1,
              pageSize: list.pageSize ? list.pageSize : 10,
            }}
            rowKey={record => {
              return record.permissionId;
            }}
          />
        </div>
      </Fragment>
    )
  }
  render() {
    return (
      <PageHeaderWrapper>
        <Card>{this.renderSearch()}</Card>
        <div className={styles.marginT}>
          <Card>
            {this.renderOpera()}
            {this.renderTable()}
          </Card>
        </div>
      </PageHeaderWrapper>
    );
  }
}
export default Form.create()(connect(({ auth, loading }) => ({
  list: auth.permissionList,
  loading: loading.effects["auth/permissionList"]
}))(Menu));
